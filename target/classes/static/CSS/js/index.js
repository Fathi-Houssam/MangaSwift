function handleMangaClick(event) {
    const mangaId = event.currentTarget.getAttribute("data-id");

    fetch('/chapter', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ mangaId: mangaId })
    })
        .then(response => response.json())
        .then(data => {
        console.log(data);
    })
        .catch(error => console.error('Error:', error));
}

document.addEventListener('DOMContentLoaded', () => {
    const mangaItems = document.querySelectorAll('.manga-item');
    mangaItems.forEach(item => {
        item.addEventListener('click', handleMangaClick);
    });




});


