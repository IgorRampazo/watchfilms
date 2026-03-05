function cadastrarFilme(){
    const movie = new Object();
    movie.title = document.forms[0].title.value;
    movie.year = document.forms[0].year.value;
    movie.genre = document.forms[0].genre.value;

    const requestOptions = {
        method: "POST",
        headers: {"Content-Type":"application/json"},
        body: JSON.stringify(movie)
    };
    fetch("http://localhost:8080/apis/add-movie", requestOptions)
        .then(response => {
            if(response.status === 200){
                return response.json()
                .then(data => {
                    alert(movie.title+" adicionado com sucesso");
                });
            }
            else {
                alert("Erro ado adicionar o filme!");
            }
            response.json()
        });
    alert(JSON.stringify(movie));
}

function cadastrarFilmePoster(){
    const formMovie = document.forms[0];

    const requestOptions = {
        method: "POST",
        body: new FormData(formMovie)
    };
    fetch("http://localhost:8080/apis/add-movie-poster", requestOptions)
        .then(response => {
            if(response.status === 200){
                return response.json()
                    .then(data => {
                        alert("Adicionado com sucesso");
                    });
            }
            else {
                alert("Erro ao adicionar o filme!");
            }
        });
}