package org.example.graphql.controllers;

import lombok.RequiredArgsConstructor;
import org.example.albumes.models.Album;
import org.example.albumes.repositories.AlbumRepository;
import org.example.artistas.models.Artista;
import org.example.artistas.repositories.ArtistasRepository;
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

    // --- QUERIES (Coinciden con el type Query del schema.graphqls) ---

    @QueryMapping
    public List<Album> albumes() {
        // Devuelve todos los álbumes
        return albumRepository.findAll();
    }

    @QueryMapping
    public Album albumById(@Argument Long id) {
        // Devuelve un álbum por su ID
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
        // Devuelve un artista por su ID
        return artistasRepository.findById(id).orElse(null);
    }

    @QueryMapping
    public List<Artista> artistasByNombre(@Argument String nombre) {
        // Devuelve artistas cuyo nombre contenga el texto (ignorando mayúsculas/minúsculas)
        // Usamos el método que ya tienes en tu repositorio: findByNombreContainingIgnoreCase
        return artistasRepository.findByNombreContainingIgnoreCase(nombre);
    }

    // --- RESOLVERS DE RELACIONES (SchemaMapping) ---

    // Cuando pides el campo "artista" dentro de un "Album"
    @SchemaMapping(typeName = "Album", field = "artista")
    public Artista artista(Album album) {
        // Devuelve el artista asociado al álbum
        return album.getArtista();
    }

    // Cuando pides el campo "albumes" dentro de un "Artista"
    @SchemaMapping(typeName = "Artista", field = "albumes")
    public List<Album> albumes(Artista artista) {
        // Devuelve la lista de álbumes de ese artista
        // Usamos el método findByArtista que deberías tener en AlbumRepository
        return albumRepository.findByArtistaId(artista.getId());
    }
}