function mountTable(json){
    let table = "";
    for(filme of json){
        table+=`<tr><td>${filme.title}</td> <td>${filme.year}</td> <td>${filme.genre}</td></tr>`
    }
    return table
}


function parseSearch(){
    const table1 = document.getElementById("tabelaSeach")
    table1.innerHTML = `
       <thead>
          <tr style="text-align: center;">
             <th>Title</th>
             <th>Year</th>
             <th>Genre</th>
             <th>Thumbnail</th>
          </tr>
       </thead>
       <tbody>
       </tbody>
    `;
    const inputSearch = document.getElementById("search1")
    console.log(inputSearch.value)
    fetch(`http://localhost:8080/apis/get-parse-movie/${inputSearch.value}`)
        .then(response =>{
            if(response.status === 200)
                return response.json()
                    .then(json =>{
                        table1.innerHTML+=mountTable(json);
                    })
            else
                alert("Não há resultados")
        })
        .catch(Error => table1.innerHTML = Error)
}
