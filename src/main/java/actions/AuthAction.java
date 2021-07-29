package actions;

import java.io.IOException;

import javax.servlet.ServletException;

import action.views.EmployeeView;
import constants.AttributeConst;
import constants.ForwardConst;
import constants.MessageConst;
import constants.PropertyConst;
import services.EmployeeService;

public class AuthAction extends ActionBase {

    private EmployeeService service ;

    @Override
    public void process() throws ServletException, IOException {
        service = new EmployeeService();

        invoke();

        service.close();
    }

    public void showLogin() throws ServletException, IOException {
        putRequestScope(AttributeConst.TOKEN, getTokenId());

        String flash = getSessionScope(AttributeConst.FLUSH);

        if(flash != null) {
            putRequestScope(AttributeConst.FLUSH, flash);
            removeSessionScope(AttributeConst.FLUSH);
        }

        forward(ForwardConst.FW_LOGIN);
    }

    public void login() throws ServletException, IOException {
        String code = getRequestParam(AttributeConst.EMP_CODE);
        String plainPass = getRequestParam(AttributeConst.EMP_PASS);
        String pepper = getContextScope(PropertyConst.PEPPER);

        Boolean isValidateEmployee = service.validateLogin(code, plainPass, pepper);

        if(isValidateEmployee) {

            if(checkToken()) {
                EmployeeView ev = service.findOne(code, plainPass, pepper);

                putRequestScope(AttributeConst.LOGIN_EMP, ev);

                putRequestScope(AttributeConst.FLUSH, MessageConst.I_LOGINED.getMessage());

                redirect(ForwardConst.ACT_TOP, ForwardConst.CMD_INDEX);

            }
        } else {
            putRequestScope(AttributeConst.EMP_CODE, code);

            putRequestScope(AttributeConst.TOKEN, getTokenId());

            putRequestScope(AttributeConst.LOGIN_ERR, true);

            forward(ForwardConst.FW_LOGIN);
            
        }
    }

}
