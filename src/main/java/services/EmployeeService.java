package services;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.NoResultException;

import action.views.EmployeeConverter;
import action.views.EmployeeView;
import constants.JpaConst;
import models.Employee;
import models.validators.EmployeeValidator;
import utils.EncryptUtil;

public class EmployeeService extends ServiceBase {

    /*
     * ページネイション
     * リストでEmployee15人のデータを返す
     */
    public List<EmployeeView> getPerPage(int page){

        List<Employee> employees = em.createNamedQuery(JpaConst.Q_EMP_GET_ALL, Employee.class)
                .setFirstResult(JpaConst.ROW_PER_PAGE * (page-1))
                .setMaxResults(JpaConst.ROW_PER_PAGE)
                .getResultList();

        return EmployeeConverter.toViewList(employees);


    }

    /*
     * 従業員の登録人数を返す
     */
    public long countAll() {
        long empCount = (long)em.createNamedQuery(JpaConst.Q_EMP_COUNT, Long.class)
                .getSingleResult();

        return empCount;

    }

    /*
     * パスワードをハッシュ化して、
     * 従業員コードとハッシュ化済みのパスワードを元に
     * EmployeeViewのインスタンスを返す
     *
     */
    public EmployeeView findOne(String code, String plainPass, String pepper) {
        Employee e = null;

        try {
            String pass = EncryptUtil.getPasswordEncrypt(plainPass, pepper);

            e = em.createNamedQuery(JpaConst.Q_EMP_GET_BY_CODE_AND_PASS, Employee.class)
                    .setParameter(JpaConst.JPQL_PARM_CODE, code)
                    .setParameter(JpaConst.JPQL_PARM_PASSWORD, pass)
                    .getSingleResult();
        } catch (NoResultException ex) {
        }

        return EmployeeConverter.toView(e);
    }


    /*
     * idを元にEmployeeViewのインスタンスを返す
     */
    public EmployeeView findOne(int id) {
        Employee e = findOneInternal(id);
        return EmployeeConverter.toView(e);
    }

    /*
     * codeに該当する従業員の件数を返す
     */
    public long countByCode(String code) {
        long employee_count = em.createNamedQuery(JpaConst.Q_EMP_COUNT_RESISTERED_BY_CODE, Long.class)
                .setParameter(JpaConst.JPQL_PARM_CODE, code)
                .getSingleResult();
        return employee_count;
    }

    /*
     * 入力された内容をDBに登録する
     * エラーがある場合エラーリストを戻す
     */
    public List<String> create(EmployeeView ev, String pepper){

        String pass = EncryptUtil.getPasswordEncrypt(ev.getPassword(), pepper);
        ev.setPassword(pass);

        LocalDateTime now = LocalDateTime.now();
        ev.setUpdatedAt(now);
        ev.setCreatedAt(now);

        List<String> errors = EmployeeValidator.validate(this, ev, true, true);

        if(errors.size() == 0) {
            create(ev);
        }

        return errors;
    }

    /*
     * 入力された更新内容を更新してDBに登録する
     * エラーがある場合はエラーリストを返す
     */
    public List<String> update(EmployeeView ev, String pepper){

        //idから変更前のsaveEmpを取得する
        EmployeeView saveEmp = findOne(ev.getId());

        boolean validateCode = false;
        if(!saveEmp.getCode().equals(ev.getCode())) {

            validateCode = true;
            saveEmp.setCode(ev.getCode());
        }

        boolean validatePass = false;
        if(ev.getPassword() != null && !ev.getPassword().equals("")) {

            validatePass = true;
            saveEmp.setPassword(EncryptUtil.getPasswordEncrypt(ev.getPassword(), pepper));
        }

        saveEmp.setName(ev.getName());
        saveEmp.setAdminFlag(ev.getAdminFlag());

        LocalDateTime now = LocalDateTime.now();
        saveEmp.setUpdatedAt(now);

        List<String> errors = EmployeeValidator.validate(this, saveEmp, validateCode, validatePass);

        if(errors.size() == 0) {
            update(saveEmp);
        }

        return errors;
    }
    /*
     * IDを元にその従業員のデータを論理削除する
     */
    public void destroy(Integer id) {

        EmployeeView saveEmp = findOne(id);

        LocalDateTime now = LocalDateTime.now();
        saveEmp.setUpdatedAt(now);

        saveEmp.setDeleteFlag(JpaConst.EMP_DEL_TRUE);

        update(saveEmp);
    }

    /*
     * codeとpassを条件に検索し、データが取得できるかboolean値を返す
     */
    public Boolean validateLogin(String code, String plainpass, String pepper) {

        boolean isValidateEmployee = false;
        
        if(code != null && !code.equals("") && plainpass != null && !plainpass.equals("")) {
            
            EmployeeView ev = findOne(code, plainpass, pepper);
            
            if(ev != null && ev.getId() != null) {
                isValidateEmployee = true;
            }

        }
        return isValidateEmployee;
    }



    /*
     * 従業員のデータを一件更新する
     */
    private void update(EmployeeView ev) {
        em.getTransaction().begin();
        Employee e = findOneInternal(ev.getId());
        EmployeeConverter.copyViewToModel(e, ev);
        em.getTransaction().commit();
    }


    /*
     * 従業員のデータを一件登録する
     */
    private void create(EmployeeView ev) {
        em.getTransaction().begin();
        em.persist(EmployeeConverter.toModel(ev));
        em.getTransaction().commit();

    }




    /*
     * idを元にEmployeeのインスタンスを返す
     */
    private Employee findOneInternal(int id) {
        Employee e = em.find(Employee.class, id);
        return e;
    }





}
