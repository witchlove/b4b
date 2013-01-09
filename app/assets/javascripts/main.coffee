$ ->
	alert("jquery is working")

	$('#test1').click -> 
		alert "Mmmmm... Coffee...."
		event.preventDefault();
		
	$j('.test').click -> 
		alert "Mmmmm... Coffee...."
		event.preventDefault();
	
	$j('#test2').click -> 
		alert "Mmmmm... Coffee.... from a button"
		event.preventDefault();
