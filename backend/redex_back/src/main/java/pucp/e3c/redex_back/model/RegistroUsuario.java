package pucp.e3c.redex_back.model;

public class RegistroUsuario {
    public Usuario usuario;
    public Cliente cliente;
    
    public RegistroUsuario() {
    }
    public RegistroUsuario(Usuario usuario, Cliente cliente) {
        this.usuario = usuario;
        this.cliente = cliente;
    }
    public Usuario getUsuario() {
        return usuario;
    }
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
    public Cliente getCliente() {
        return cliente;
    }
    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    
}
