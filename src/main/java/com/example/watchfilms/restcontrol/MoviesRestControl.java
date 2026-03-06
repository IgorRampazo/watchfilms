package com.example.watchfilms.restcontrol;

import com.example.watchfilms.entities.Erro;
import com.example.watchfilms.entities.Movie;
import com.example.watchfilms.repositories.MovieRepository;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("apis")
public class MoviesRestControl {

    @Autowired
    private MovieRepository movieRepository;

    @GetMapping("test")
    public ResponseEntity<Object> test(){
        return ResponseEntity.ok("ok");
    }

    @GetMapping("random-movie")
    public ResponseEntity<Object> randomMovie(){
        List<Movie> movieList = movieRepository.getMovies();
        Random rand = new Random();
        return ResponseEntity.ok().body(movieList.get(rand.nextInt(movieList.size())));
    }

    @GetMapping("list-movie")
    public ResponseEntity<Object> listMovies(){
        List<Movie> movieList = movieRepository.getMovies();
        return ResponseEntity.ok().body(movieList);
    }

    @GetMapping("get-movie")
    public ResponseEntity<Object> getMovie(@RequestParam(value = "name") String name){
        Movie get = searchMovie(name);
        if(get != null)
            return ResponseEntity.ok().body(get);
        else return ResponseEntity.badRequest().body(new Erro("Título Inexistente", ""));
    }

    @GetMapping("get-movie/{name}")
    public ResponseEntity<Object> getMovieP(@PathVariable String name){
        Movie get = searchMovie(name);
        if(get != null)
            return ResponseEntity.ok().body(get);
        else return ResponseEntity.badRequest().body(new Erro("Título Inexistente", ""));
    }

    @GetMapping("get-parse-movie/{parse}")
    public ResponseEntity<Object> getMovieParseName(@PathVariable String parse){
        List<Movie> get = searchInList(parse);
        System.out.println(get.size());
        if(!get.isEmpty())
            return ResponseEntity.ok().body(get);
        else return ResponseEntity.badRequest().body(new Erro("Empty", ""));
    }

    private Movie searchMovie(String name){
        List<Movie> movieList = movieRepository.getMovies();
        Movie get = null;
        for(Movie m : movieList){
            if(m.getTitle().equals(name))
                get = m;
        }
        return get;
    }

    private List<Movie> searchInList(String name){
        List<Movie> movieList = movieRepository.getMovies();
        List<Movie> get = new ArrayList<>();
        for(Movie m : movieList){
            if(m.getTitle().toUpperCase().contains(name.toUpperCase()))
                get.add(m);
        }
        System.out.println(get.size());
        return get;
    }

    @GetMapping("list-genre/{genre}")
    public ResponseEntity<Object> getGenre(@PathVariable String genre){
        List<Movie> doador = movieRepository.getMovies();
        List<Movie> receptor = new ArrayList<>();
        for(Movie m : doador){
            if(m.getGenre().equals(genre))
                receptor.add(m);
        }
        if(receptor.isEmpty())
            return ResponseEntity.badRequest().body(new Erro("Gênero Inexistente", ""));
        else
            return ResponseEntity.ok().body(receptor);
    }

    @GetMapping("list-year")
    public ResponseEntity<Object> getGenre(@RequestParam(value = "dtI") String dtI, @RequestParam(value = "dtF") String dtF){
        List<Movie> doador = movieRepository.getMovies();
        List<Movie> receptor = new ArrayList<>();
        int anoM;
        for(Movie m : doador){
            anoM = Integer.parseInt(m.getYear());
            if(anoM >= Integer.parseInt(dtI)  && anoM <= Integer.parseInt(dtF))
                receptor.add(m);
        }
        if(receptor.isEmpty())
            return ResponseEntity.badRequest().body(new Erro("404", "Nenhum filme foi encontrado neste período"));
        else
            return ResponseEntity.ok().body(receptor);
    }

    @PostMapping("add-movie")
    public ResponseEntity<Object> addMovie(@RequestBody Movie movie){
        if(movie == null || movie.getTitle().isEmpty() || movie.getGenre() == null || movie.getGenre().isEmpty() || movie.getTitle() == null){
            return ResponseEntity.badRequest().body(new Erro("INCOMPLETE INTELS", "Enter all informations (TITLE, YEAR OF PRODUCTION AND GENRE)"));
        }
        movieRepository.getMovies().add(movie);
        return ResponseEntity.ok().body(movie);
    }

    @PostMapping("add-movie-poster")
    public ResponseEntity<Object> addMoviePoster(String title, String year, String genre, MultipartFile poster)
    {
        Path uploadPath = Paths.get("uploads/images").toAbsolutePath();

        try
        {
            Files.createDirectories(uploadPath);
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        Movie movie = new Movie(title, year, genre);
        String filename = "404.png";

        if(poster != null && !poster.isEmpty())
        {
            try
            {
                filename = Paths.get(poster.getOriginalFilename()).getFileName().toString();

                File file = uploadPath.resolve(filename).toFile();

                poster.transferTo(file);

                // ----- thumb ----- //
                Path thumbPath = Paths.get("uploads/thumbs").toAbsolutePath();
                Files.createDirectories(thumbPath);

                String thumbName = "thumb_" + filename.replaceAll("\\..*$", ".jpg");
                File thumbFile = thumbPath.resolve(thumbName).toFile();

                Thumbnails.of(file).size(100,100).outputFormat("jpg").toFile(thumbFile);
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }

        movie.setPoster(filename);

        movieRepository.getMovies().add(movie);

        return ResponseEntity.ok(movie);
    }

    @Configuration
    public class WebConfig implements WebMvcConfigurer
    {
        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry)
        {
            registry.addResourceHandler("/images/**").addResourceLocations("file:uploads/images/");
            registry.addResourceHandler("/thumbs/**").addResourceLocations("file:uploads/thumbs/");
        }
    }
}
