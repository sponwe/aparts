package br.com.tomvieira.aparts.mbeans;

import br.com.tomvieira.aparts.ejb.EmpreendimentoManager;
import br.com.tomvieira.aparts.model.Empreendimento;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Init;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

/**
 *
 * @author Wellington
 */
@Named
@ViewScoped
public class EmpreendimentoMB implements Serializable{
    private static final long serialVersionUID = 1482935518770183313L;
    
    private List<Empreendimento> empreendimentos;
    @EJB
    private EmpreendimentoManager empreendimentoManager;
    
    @PostConstruct
    private void init(){
        empreendimentos = empreendimentoManager.getAll();
    }

    public List<Empreendimento> getEmpreendimentos() {
        return empreendimentos;
    }
            
}
