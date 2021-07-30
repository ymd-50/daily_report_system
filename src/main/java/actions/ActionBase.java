package actions;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import constants.AttributeConst;
import constants.ForwardConst;
import constants.PropertyConst;

public abstract class ActionBase {
    protected ServletContext context;
    protected HttpServletRequest request;
    protected HttpServletResponse response;

    /*
     *  初期化処理
     *  @param servletContext
     *  @param servletRequest
     *  @param servletresponse
     */
    public void init(
            ServletContext context,
            HttpServletRequest request,
            HttpServletResponse response) {

        this.context = context;
        this.request = request;
        this.response = response;

    }

    /*
     * フロントコントローラーから呼び出されるメソッド
     */
    public abstract void process() throws ServletException, IOException;


    /*
     * パラメータcommandに該当するメソッドを実行する
     */
    protected void invoke() throws ServletException, IOException{
        Method commandMethod;

        try {
            String command = request.getParameter(ForwardConst.CMD.getValue());

            commandMethod = this.getClass().getDeclaredMethod(command, new Class[0]);
            commandMethod.invoke(this, new Object[0]);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            System.err.println("error->" + e.getCause());
            forward(ForwardConst.FW_ERR_UNKNOWN);

        }
    }


    /*
     *  指定されたjspファイルを呼び出す
     */
    protected void forward(ForwardConst target) throws ServletException,IOException{
        String forward = String.format("/WEB-INF/views/%s.jsp", target.getValue());
        RequestDispatcher dispatcher = request.getRequestDispatcher(forward);
        dispatcher.forward(request, response);

    }


    /*
     * 引数からURLを作り、リダイレクトを行う
     */
    protected void redirect(ForwardConst action, ForwardConst command)
            throws ServletException, IOException{

        String redirectUrl = request.getContextPath() + "/?action=" + action.getValue();

        if(command != null) {
            redirectUrl = redirectUrl + "&command=" + command.getValue();
        }

        response.sendRedirect(redirectUrl);

    }


    /*
     * token不正の場合はfalseを返し、エラー画面を表示
     * 不正が無い場合はtrueを返す
     */
    protected boolean checkToken() throws ServletException, IOException{

        String _token = getRequestParam(AttributeConst.TOKEN);

        if(_token == null || !(_token.equals(getTokenId()))) {

            forward(ForwardConst.FW_ERR_UNKNOWN);
            return false;

        }else {
            return true;
        }
    }


    /*
     * セッションIDを取得する
     */
    protected String getTokenId() {
        return request.getSession().getId();
    }

    /*
     * パラメータで指定されたページ数を返す
     * パラメータの指定が無い場合は１を返す
     */
    protected int getPage() {
        int page;
        page = toNumber(request.getParameter(AttributeConst.PAGE.getValue()));
        if(page == Integer.MIN_VALUE) {
            page = 1;
        }
        return page;
    }


    /*
     * 文字列を数値に変換する
     * 文字列が数値に変換できない場合はInteger.MIN_VALUEを返す
     */
    protected int toNumber(String strNumber) {
        int number = 0;
        try {
            number = Integer.parseInt(strNumber);
        } catch (Exception e) {
            number = Integer.MIN_VALUE;
        }
        return number;
    }

    /*
     * 文字列をLocalDateに変換する
     * 文字列が空白の場合は現在時刻を返す
     */
    protected LocalDate toLocalDate(String strDate) {
        if(strDate == null || strDate.equals("")) {
            return LocalDate.now();
        }
        return LocalDate.parse(strDate);
    }

    /*
     * 指定されたパラメータからrequestスコープの値を取得する
     */
    protected String getRequestParam(AttributeConst key) {
        return request.getParameter(key.getValue());
    }

    protected <V> void putRequestScope(AttributeConst key, V value) {
        request.setAttribute(key.getValue(), value);
    }

    @SuppressWarnings("unchecked")
    protected <R> R getSessionScope(AttributeConst key) {
        return (R) request.getSession().getAttribute(key.getValue());

    }

    protected <V> void putSessionScope(AttributeConst key, V value) {
        request.getSession().setAttribute(key.getValue(), value);
    }

    protected void removeSessionScope(AttributeConst key) {
        request.getSession().removeAttribute(key.getValue());
    }

    @SuppressWarnings("unchecked")
    protected <R> R getContextScope(PropertyConst key) {
        return (R) context.getAttribute(key.getValue());    }



}
