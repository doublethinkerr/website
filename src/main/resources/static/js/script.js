$('.single-item').slick({
  infinite: true,
  speed: 1000,
  fade: true,
  cssEase: 'linear',
  autoplay: true,
  autoplaySpeed: 1000,
});

$(function() {
  $('.blue-bar1 [href]').each(function() {
    if (this.href == window.location.href) {
      $(this).addClass('active');
    }
  });
});

Fancybox.bind('[data-fancybox="gallery"]', {
  caption: function (fancybox, carousel, slide) {
    return (
      `${slide.index + 1} / ${carousel.slides.length} <br />` + slide.caption
    );
  },
});

