package models.validators;

import java.util.ArrayList;
import java.util.List;

import actions.views.EmployeeView;
import constants.MessageConst;
import services.EmployeeService;

public class EmployeeValidator {

    //入力値からエラーを見つけエラー文を返す
    public static List<String> validate(
            EmployeeService service,
            EmployeeView ev,
            Boolean codeDuplicateCheckFlag,
            Boolean passwordCheckFlag
            ){
        List<String> errors = new ArrayList<>();


        String codeError = validateCode(service, ev.getCode(), codeDuplicateCheckFlag);
        if(!codeError.equals("")) {
            errors.add(codeError);
        }

        String nameError = validateName(ev.getName());
        if(!nameError.equals("")) {
            errors.add(nameError);
        }

        String passwordError = validatePassword(ev.getPassword(), passwordCheckFlag);
        if(!passwordError.equals("")) {
            errors.add(passwordError);
        }
        return errors;




    }


    //codeが重複していないかチェック
    private static String validateCode(EmployeeService service, String code, Boolean codeDuplicateCheckFlag) {

        if(code == null || code == "") {
            return MessageConst.E_NOEMP_CODE.getMessage();
        }

        if(codeDuplicateCheckFlag) {
            long employeeCount = isDuplicateEmployee(service, code);

            if(employeeCount > 0) {
                return MessageConst.E_EMP_CODE_EXIST.getMessage();
            }
        }

        return "";
    }

    //codeに該当する従業員の件数を返す
    private static long isDuplicateEmployee(EmployeeService service, String code) {
        long employeesCount = service.countByCode(code);
        return employeesCount;
    }


    //nameが空かどうかチェック
    private static String validateName(String name) {
        if(name == null || name == "") {
            return MessageConst.E_NONAME.getMessage();
        }
        return "";
    }


    //passwordの入力値のチェック
    private static String validatePassword(String password, Boolean passwordCheckFlag) {
        if(passwordCheckFlag && (password == null || password == "")) {
            return MessageConst.E_NOPASSWORD.getMessage();
        }
        return "";
    }
}
