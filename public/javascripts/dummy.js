$(document).ready(function() {
  // your code
  alert('Page: ' + $('title').html() + ' dom loaded!');
  
  $('.test').click(function() {
      alert("Mmmmm... Coffee....");
      jsRoutes.controllers.OrderController.create().ajax({
          success : function(data) {
              alert(data);
          },
          error : function(err) {
              alert(err);             
          }
      });
    });
  
  $('#addorderitem').click(function() {
//	  alert("product val " +  $('#productSelector option:selected').val())
//	  alert("product text " +  $('#productSelector option:selected').text())
//	  alert("product id " +  $('#productSelector option:selected').attr('id'))
	  var clone = $('#firstOrderItem').clone().attr('id','')
	  
	  alert(clone.children('input').val())
	  clone.insertAfter('#firstOrderItem')
  });
  
  $('#target').submit(function() {
	  alert('Handler for .submit() called.');
	  jsRoutes.controllers.OrderController.orderitem().ajax({
          success : function(data) {
              alert(data);
          },
          error : function(err) {
              alert(err);             
          }
      });
	  return false;
	});
  
  $('#productSelector33').change(function(){
	  var optionValue = jQuery("select[id='productSelector']").val();
	  alert('changed to ' + optionValue)
	  $('#products').html("<option id=test3 value='test'>hello world</option>" +
	  		"				<option id=test3 value='test'>hello world</option>" +
	  		"				<option id=test3 value='test'>hello world</option>" +
	  		"				<option id=test3 value='test'>hello world</option>" +
	  		"				<option id=test3 value='test'>hello world</option>" +
	  		"				<option id=test3 value='test'>hello world</option>")
  });
  
  
  $('#productSelector').change(function(){
	  var optionValue = jQuery("select[id='productSelector']").val();
	  jsRoutes.controllers.ProductController.productlistForId(optionValue).ajax({
          success : function(data) {
        	  alert("succes")
        	  var parsed = jQuery.parseJSON(data)
              alert(data);
          },
          error : function(err) {
              alert(err);             
          }
      });
	  alert('changed to ' + optionValue)
  });
  
})