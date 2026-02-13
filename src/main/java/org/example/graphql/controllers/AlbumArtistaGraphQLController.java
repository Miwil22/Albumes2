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
// @PreAuthorize("hasAnyRole('USER')") // Protección a nivel de clase
public class AlbumArtistaGraphQLController {
  private final AlbumRepository albumRepository;
  private final ArtistasRepository artistasRepository;


  // --- QUERIES ---

  @QueryMapping
  public List<Album> albumes() {
    // Devuelve todos los albumes como entidades (ojo: no paginado)
    return albumRepository.findAll();
  }

  @QueryMapping
  public Album albumById(@Argument Long id) {
    // Devuelve un album por su id
    Optional<Album> albumOpt = albumRepository.findById(id);
    return albumOpt.orElse(null);
  }

  @QueryMapping
  public List<Artista> artistas() {
    // Devuelve todos los artistas como entidades
    return artistasRepository.findAll();
  }

  @QueryMapping
  public Artista artistaById(@Argument Long id) {
    // Devuelve un artista por id
    return artistasRepository.findById(id).orElse(null);
  }

  // artistasByNombre(nombre: String!): [Artista!]!
  @QueryMapping
  public List<Artista> artistasByNombre(@Argument String nombre) {
    // Devuelve los artistas que coinciden con el nombre (case insensitive)
    return artistasRepository.findByNombreContainingIgnoreCase(nombre);
    // En caso de que no encuentre ninguna, devuelve una lista vacía
  }

  // --- RESOLVERS RELACIONES ---

  @SchemaMapping(typeName = "Album", field = "artista")
  public Artista artista(Album album) {
    // Devuelve el artista del album (ya viene cargada en la entidad)
    return album.getArtista();
  }

  @SchemaMapping(typeName = "Artista", field = "albumes")
  public List<Album> albumes(Artista artista) {
    // Devuelve los albumes de un artista
    return albumRepository.findByArtista(artista);
  }
}