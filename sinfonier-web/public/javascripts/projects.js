$(function(){
	$('.js-project-use').click(function(){
		var id = $(this).data('id');
		if (id ==  ''){
			$('<form action="/projects/deactivate" method="POST"></form>').appendTo('body').submit();
		} else {
			$('<form action="/projects/'+id+'/activate" method="POST"></form>').appendTo('body').submit();
		}
	});
});