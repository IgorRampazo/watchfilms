function carregarFilmeAleatorio(){
    const divR = document.getElementById("recom");
    fetch("http://localhost:8080/apis/random-movie")
        .then(r => r.json())
        .then(json => {
            console.log(json.title)
            divR.innerHTML = `Name: ${json.title} <br> Year of release: ${json.year}`
        })
        .catch(Error => divR.innerHTML= Error)

}