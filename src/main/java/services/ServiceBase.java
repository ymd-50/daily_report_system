package services;

import javax.persistence.EntityManager;

import utils.DBUtil;

//DB共通処理
public class ServiceBase {

    protected EntityManager em = DBUtil.createEntityManager();

    public void close() {
        if(em.isOpen()) {
            em.close();
        }
    }
}
