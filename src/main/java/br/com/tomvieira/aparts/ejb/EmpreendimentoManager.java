package br.com.tomvieira.aparts.ejb;

import br.com.tomvieira.aparts.model.Empreendimento;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Wellington
 */
@Stateless
public class EmpreendimentoManager extends BaseManager<Empreendimento, Long>{

    @PersistenceContext(unitName = "apartsPU")
    private EntityManager em;

    @Override
    public EntityManager getEm() {
        return em;
    }

    @Override
    public List<Empreendimento> encontrarIgual(Empreendimento o) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }
    
    
}
