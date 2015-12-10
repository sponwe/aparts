package br.com.tomvieira.aparts.ejb;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

public abstract class BaseManager<T, K> {

    private Class<T> objClass;

    public abstract EntityManager getEm();

    public abstract List<T> encontrarIgual(T o);    

    @SuppressWarnings("unchecked")
    public BaseManager() {
        Type superclass = getClass().getGenericSuperclass();
        for (int i = 0; i < 2; i++) {
            if (superclass instanceof Class) {
                superclass = ((Class<?>) superclass).getGenericSuperclass();
            }
        }
        Type type = ((ParameterizedType) superclass).getActualTypeArguments()[0];
        //System.out.println("Class: "+ type);
        objClass = (Class<T>) type;
    }

    public Class<T> getObjClass() {
        return objClass;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public void setObjClass(Class objClass) {
        this.objClass = objClass;
    }

    public T alterar(T entity) {
        entity = getEm().merge(entity);        
        return entity;
    }

    public void excluir(T entity) {
        entity = getEm().merge(entity);
        getEm().remove(entity);        
    }

    public T salvar(T entity) throws Exception {
        try {
            getEm().persist(entity);
        } catch (ConstraintViolationException cve) {
            tratarMsg(cve);
        }        
        return entity;
    }

    @SuppressWarnings("rawtypes")
    private void tratarMsg(ConstraintViolationException cve) throws Exception {
        String msgTratada = null;
        try {
            ConstraintViolation cv = cve.getConstraintViolations().iterator().next();
            String field = cv.getPropertyPath().toString();
            String msg = cv.getMessage();
            msgTratada = "Erro no campo " + field + " de " + objClass + ": " + msg;
        } catch (Exception e) {
            msgTratada = null;
        }
        if (msgTratada != null) {
            throw new RuntimeException(msgTratada);
        } else {
            throw cve;
        }
    }

    @SuppressWarnings("unchecked")
    public List<T> getAll() {
        Query q = getEm().createQuery("SELECT a FROM " + objClass.getSimpleName() + " a ");
        return q.getResultList();
    }

    public T getById(K id) {
        if (id == null) {
            return null;
        }
        return (T) getEm().find(objClass, id);
    }

    public long count() {
        Query q = getEm().createQuery("SELECT COUNT(a) FROM " + objClass.getSimpleName() + " a ");
        return (Long) q.getSingleResult();
    }

   

    public boolean isEmptyOrNull(String s) {
        return s == null || s.trim().equals("");
    }


    protected boolean isZeroOrNull(BigDecimal valor) {
        return valor == null || valor.compareTo(BigDecimal.ZERO) == 0;
    }

    protected boolean isZeroOrNull(Long valor) {
        return valor == null || valor == 0;
    }

    protected boolean isZeroOrNull(Integer valor) {
        return valor == null || valor == 0;
    }

    
    /**
     * Verifica a repetição de um registro.
     *
     * @param entity Entidade a ser incluída ou alterada
     * @param erro Erro se o objeto for repetido
     *
     * @throws BusinessException
     */
    protected void checarRepeticao(T entity, String erro) throws BusinessException {
        List<T> lista = encontrarIgual(entity);
        if (lista.isEmpty()) {
            return;
        }
        for (T t : lista) {
            if (!t.equals(entity)) {
                throw new BusinessException(erro);
            }
        }
    }
      
}
