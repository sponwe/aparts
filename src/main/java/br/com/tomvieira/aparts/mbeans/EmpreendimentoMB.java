package br.com.tomvieira.aparts.mbeans;

import br.com.tomvieira.aparts.ejb.EmpreendimentoManager;
import br.com.tomvieira.aparts.ejb.JsfUtil;
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
    
    private Empreendimento empreendimento;
    
    @PostConstruct
    private void init(){
        empreendimentos = empreendimentoManager.getAll();
    }

    public List<Empreendimento> getEmpreendimentos() {
        return empreendimentos;
    }
    
    public String salvar(){
        try {            
           empreendimentoManager.salvar(empreendimento);
            JsfUtil.addSuccessMessage("Área onerada salva com sucesso");            
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Erro ao salvar empreendimento");
        }
        return null;
    }
       
            
}
