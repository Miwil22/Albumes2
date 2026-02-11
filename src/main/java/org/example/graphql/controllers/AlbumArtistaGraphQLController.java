package org.example.graphql.controllers;

import org.example.rest.albumes.models.Album;
import org.example.rest.albumes.repositories.AlbumRepository;
import org.example.rest.artistas.models.Artista;
import org.example.rest.artistas.repositories.ArtistasRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Controller
// @PreAuthorize("hasAnyRole('USER')") // Puedes descomentar esto si quieres seguridad
public class AlbumArtistaGraphQLController {

    private final AlbumRepository albumRepository;
    private final ArtistasRepository artistasRepository;

    // --- QUERIES (Consultas de lectura) ---

    @QueryMapping
    public List<Album> albumes() {
        // Devuelve todos los álbumes de la base de datos
        return albumRepository.findAll();
    }

    @QueryMapping
    public Album albumById(@Argument Long id) {
        // Devuelve un álbum buscando por su ID
        Optional<Album> albumOpt = albumRepository.findById(id);
        return albumOpt.orElse(null);
    }

    @QueryMapping
    public List<Artista> artistas() {
        // Devuelve todos los artistas
        return artistasRepository.findAll();
    }

    @QueryMapping
    public Artista artistaById(@Argument Long id) {
        // Devuelve un artista buscando por su ID
        return artistasRepository.findById(id).orElse(null);
    }

    @QueryMapping
    public List<Artista> artistasByNombre(@Argument String nombre) {
        // Busca artistas cuyo nombre contenga el texto (ignorando mayúsculas/minúsculas)
        // Asegúrate de tener este método en tu ArtistasRepository o usa findAll() y filtra con Java stream
        return artistasRepository.findByNombreContainingIgnoreCase(nombre);
    }

    // --- RESOLVERS DE RELACIONES (SchemaMapping) ---  los get los tengo aqui

    // Cuando alguien pide el campo "artista" dentro de un "Album"
    @SchemaMapping(typeName = "Album", field = "artista")
    public Artista artista(Album album) {
        // Devuelve el objeto Artista asociado al álbum
        return album.getArtista();
    }

    // Cuando alguien pide el campo "albumes" dentro de un "Artista"
    @SchemaMapping(typeName = "Artista", field = "albumes")
    public List<Album> albumes(Artista artista) {
        // Busca en la BD todos los álbumes que tengan el ID de este artista
        return albumRepository.findByArtistaId(artista.getId());
    }
}