package Modelo;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

public class Libro {
    private Integer idLibro;
    private String titulo;
    private String isbn;
    private Date fechaAdquisicion;
    private Integer idEstado;
    
    // Relaciones
    private List<Autor> autores;
    private List<Genero> generos;
    private List<Categoria> categorias;

    // Constructor
    public Libro() {
        this.autores = new ArrayList<>();
        this.generos = new ArrayList<>();
        this.categorias = new ArrayList<>();
    }

    // Getters y Setters
    public Integer getIdLibro() {
        return idLibro;
    }

    public void setIdLibro(Integer idLibro) {
        this.idLibro = idLibro;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public Date getFechaAdquisicion() {
        return fechaAdquisicion;
    }

    public void setFechaAdquisicion(Date fechaAdquisicion) {
        this.fechaAdquisicion = fechaAdquisicion;
    }

    public Integer getIdEstado() {
        return idEstado;
    }

    public void setIdEstado(Integer idEstado) {
        this.idEstado = idEstado;
    }

    public List<Autor> getAutores() {
        return autores;
    }

    public void setAutores(List<Autor> autores) {
        this.autores = autores;
    }

    public List<Genero> getGeneros() {
        return generos;
    }

    public void setGeneros(List<Genero> generos) {
        this.generos = generos;
    }

    public List<Categoria> getCategorias() {
        return categorias;
    }

    public void setCategorias(List<Categoria> categorias) {
        this.categorias = categorias;
    }

    @Override
    public String toString() {
        return "Libro{" +
                "idLibro=" + idLibro +
                ", titulo='" + titulo + '\'' +
                ", isbn='" + isbn + '\'' +
                ", fechaAdquisicion=" + fechaAdquisicion +
                ", idEstado=" + idEstado +
                ", autores=" + autores +
                ", generos=" + generos +
                ", categorias=" + categorias +
                '}';
    }
}