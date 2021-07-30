package actions.views;

import java.util.ArrayList;
import java.util.List;

import constants.AttributeConst;
import constants.JpaConst;
import models.Employee;

//  入力値からDBに変換してインスタンスを返す
public class EmployeeConverter {
    public static Employee toModel (EmployeeView ev) {

        return new Employee(
                ev.getId(),
                ev.getCode(),
                ev.getName(),
                ev.getPassword(),
                ev.getAdminFlag() == null
                    ?null
                    :ev.getAdminFlag() == AttributeConst.ROLE_ADMIN.getIntegerValue()
                        ?JpaConst.ROLE_ADMIN
                        :JpaConst.ROLE_GENERAL,
                ev.getCreatedAt(),
                ev.getUpdatedAt(),
                ev.getDeleteFlag() == null
                    ?null
                    :ev.getDeleteFlag() == AttributeConst.DEL_FLAG_TRUE.getIntegerValue()
                        ?JpaConst.EMP_DEL_TRUE
                        :JpaConst.EMP_DEL_FALSE
                );
    }



    //DBから入力に変換してインスタンスを返す
    public static EmployeeView toView(Employee e) {
        if(e == null) {
            return null;
        }

        return new EmployeeView(
                e.getId(),
                e.getCode(),
                e.getName(),
                e.getPassword(),
                e.getAdminFlag() == null
                    ?null
                    :e.getAdminFlag() == JpaConst.ROLE_ADMIN
                        ?AttributeConst.ROLE_ADMIN.getIntegerValue()
                        :AttributeConst.ROLE_GENERAL.getIntegerValue(),
                e.getCreatedAt(),
                e.getUpdatedAt(),
                e.getDeleteFlag() == null
                    ?null
                    :e.getDeleteFlag() == JpaConst.EMP_DEL_TRUE
                        ?AttributeConst.DEL_FLAG_TRUE.getIntegerValue()
                        :AttributeConst.DEL_FLAG_FALSE.getIntegerValue()
                );
    }


    //DBのリストからViewのリストに詰め替え
    public static List<EmployeeView> toViewList(List<Employee> list){
        List<EmployeeView> evs = new ArrayList<>();

        for(Employee e : list) {
            evs.add(toView(e));
        }

        return evs;
    }


    //入力値をDBに詰める
    public static void copyViewToModel(Employee e, EmployeeView ev) {
        e.setAdminFlag(ev.getAdminFlag());
        e.setCode(ev.getCode());
        e.setCreatedAt(ev.getCreatedAt());
        e.setDeleteFlag(ev.getDeleteFlag());
        e.setId(ev.getId());
        e.setName(ev.getName());
        e.setPassword(ev.getPassword());
        e.setUpdatedAt(ev.getUpdatedAt());
    }


}
