$(document).ready(function() {
	$('.recategorize').click(function() {
		$(this).parents('.span2').prev().removeClass().addClass('span7 ' + $(this).text());
		var id = $(this).parents('.span2').prev().attr('id');
		$.getJSON('recategorize?id=' + id + '&newCategory=' + $(this).text());
	});
});
