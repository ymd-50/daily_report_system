package filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import actions.views.EmployeeView;
import constants.AttributeConst;
import constants.ForwardConst;

/**
 * Servlet Filter implementation class LoginFilter
 */
@WebFilter("/*")
public class LoginFilter implements Filter {



    /**
     * Default constructor.
     */
    public LoginFilter() {
    }

    /**
     * @see Filter#destroy()
     */
    public void destroy() {
    }

    /**
     * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        // TODO Auto-generated method stub
        // place your code here
        String contextPath = ((HttpServletRequest)request).getContextPath();
        String servletPath = ((HttpServletRequest)request).getServletPath();

        if(servletPath.matches("/css.*")) {
            //cssファイルはここで終わり（フィルタをかけない）
            chain.doFilter(request, response);
        } else {
            HttpSession session = ((HttpServletRequest)request).getSession();

            String action = request.getParameter(ForwardConst.ACT.getValue());
            String command = request.getParameter(ForwardConst.CMD.getValue());

            EmployeeView ev = (EmployeeView) session.getAttribute(AttributeConst.LOGIN_EMP.getValue());

            if(ev == null) {
                //ログインしていない時
                if(!(ForwardConst.ACT_AUTH.getValue().equals(action)
                        && (ForwardConst.CMD_SHOW_LOGIN.getValue().equals(command)
                                || ForwardConst.CMD_LOGIN.getValue().equals(command)))) {
                    ((HttpServletResponse) response).sendRedirect(
                            contextPath
                            + "?action=" + ForwardConst.ACT_AUTH.getValue()
                            + "&command=" + ForwardConst.CMD_LOGIN.getValue());
                    return;
                }
            } else {
                //ログインしている時
                if(ForwardConst.ACT_AUTH.getValue().equals(command)) {
                    //認証系アクション時
                    if(ForwardConst.CMD_SHOW_LOGIN.getValue().equals(command)) {
                        ((HttpServletResponse) response).sendRedirect(
                                contextPath
                                + "?action=" + ForwardConst.ACT_TOP.getValue()
                                + "&command=" + ForwardConst.CMD_INDEX.getValue());
                        return;
                    }
                } else if (ForwardConst.CMD_LOGOUT.getValue().equals(command)) {

                } else {
                  //上記以外の認証系Actionはエラー画面

                    String forward = String.format("/WEB-INF/views/%s.jsp", "error/unknown");
                    RequestDispatcher dispatcher = request.getRequestDispatcher(forward);
                    dispatcher.forward(request, response);

                    return;
                }
            }
        }
        // pass the request along the filter chain
        chain.doFilter(request, response);
    }

    /**
     * @see Filter#init(FilterConfig)
     */
    public void init(FilterConfig fConfig) throws ServletException {
    }

}
