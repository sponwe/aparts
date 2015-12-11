package br.com.tomvieira.aparts.mbeans;

import br.com.tomvieira.aparts.ejb.BusinessException;
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
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author Wellington
 */
@Named
@ViewScoped
public class EmpreendimentoMB implements Serializable {

    private static final long serialVersionUID = 1482935518770183313L;

    private List<Empreendimento> empreendimentos;
    @EJB
    private EmpreendimentoManager empreendimentoManager;

    private Empreendimento empreendimento;

    @PostConstruct
    private void init() {
        atualizaLista();
        novo();
    }

    public List<Empreendimento> getEmpreendimentos() {
        return empreendimentos;
    }

    public String salvar() {
        try {
            validar();
            empreendimentoManager.salvar(empreendimento);
            JsfUtil.addSuccessMessage("Empreendimento salvo com sucesso");
            novo();
            atualizaLista();
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Erro ao salvar empreendimento");
        }
        return null;
    }
    
    private void validar() throws BusinessException{
        if (StringUtils.isBlank(empreendimento.getNome())){
            throw  new BusinessException("Informe o nome.");
        }
        if (StringUtils.isBlank(empreendimento.getEndereco())){
            throw  new BusinessException("Informe o endereço.");
        }
        if (StringUtils.isBlank(empreendimento.getZona())){
            throw  new BusinessException("Informe a zona.");
        }
        if (StringUtils.isBlank(empreendimento.getTipoVagas())){
            throw  new BusinessException("Informe o tipo das vagas.");
        }
    }

    public void novo (){
        empreendimento = new Empreendimento();
    }

    public Empreendimento getEmpreendimento() {
        return empreendimento;
    }

    public void setEmpreendimento(Empreendimento empreendimento) {
        this.empreendimento = empreendimento;
    }

    private void atualizaLista() {
        empreendimentos = empreendimentoManager.getAll();
    }
        
}
