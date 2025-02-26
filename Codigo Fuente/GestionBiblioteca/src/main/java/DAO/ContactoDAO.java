package DAO;

import Modelo.Contacto;
import java.util.List;

public interface ContactoDAO extends DAO<Contacto> {
    void eliminarTodos() throws Exception;
    List<Contacto> buscarPorUsuario(Integer idUsuario) throws Exception;
    List<Contacto> buscarPorTipo(Integer idTipoContacto) throws Exception;
    Contacto buscarPorValor(String valor) throws Exception;
    void verificarContacto(Integer idContacto) throws Exception;
    List<Contacto> obtenerContactosVerificados() throws Exception;
    List<Contacto> obtenerContactosPendientes() throws Exception;
}