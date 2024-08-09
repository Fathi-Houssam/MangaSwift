function redirectToChapterInfo(chapterId) {
    window.location.href = '/chapter-info?chapterId=' + chapterId;
}

document.addEventListener('DOMContentLoaded', () => {
    const chapterItems = document.querySelectorAll('.chapter-item');

    chapterItems.forEach(item => {
        item.addEventListener('click', function () {
            const chapterId = this.getAttribute('data-id');
            redirectToChapterInfo(chapterId);
        });
    });
});
