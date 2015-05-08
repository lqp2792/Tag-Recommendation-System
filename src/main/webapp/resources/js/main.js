$(document).on('click', '#register-menu', function(e) {
	$('#login-modal').modal('hide');
});
$(document).on('click', '#login-menu', function(e) {
	$('#register-modal').modal('hide');
});
$(document).ready(function(e) {
	$('.tlt-h').textillate({
		minDisplayTime: 7000,  
	    out :{  delay: 3, effect: 'lightSpeedOut'},
	    loop: true
	});
	$('.tlt-p').textillate({
	    minDisplayTime: 1000, 
	    out :{  delay: 3, effect: 'lightSpeedOut'},
	    loop: true
	});
	/* center modal */
	function centerModals() {
		$('.modal').each(function(i) {
			var $clone = $(this).clone().css('display', 'block').appendTo('body');
			var top = Math.round(($clone.height() - $clone.find('.modal-content').height()) / 2);
			top = top > 0 ? top : 0;
			$clone.remove();
			$(this).find('.modal-content').css("margin-top", top);
		});
	}
	$('.modal').on('show.bs.modal', centerModals);
	$(window).on('resize', centerModals);
	// Login ajax
	$('#login-form').submit(
	function(e) {
		var submit = false;
		var email = $('#login-email').val();
		var password = $('#login-password').val();
		e.preventDefault();
		if (!email || !password) {
			submit = false;
			if (!email) {
				$('#login-email').parent().addClass('has-error');
				$('#login-email').parent().find('.error-message').html('<p class="text-danger error-message">' 
						+ '<i class="fa fa-times"></i> Email can not be blank!</p>')
			}
			if (!password) {
				$('#login-password').parent().addClass('has-error');
				$('#login-password').parent().find('.error-message').html('<p class="text-danger error-message">'
						+ '<i class="fa fa-times"></i> Password can not be blank!</p>')
			}
		} else {
			submit = true;
		}
		if (submit === true) {
			$.ajax({
				type : 'POST',
				url : "/TagRecommend/login",
				data : {
					'email' : email,
					'password' : password
				},
				success : function(data) {
					if (data.status == "SUCCESS") {
						window.location.href = "/TagRecommend/dashboard";
					} else {
						$('.modal-title').parent().find('.error-message').html('<p class="><br/><i class="fa fa-times"></i>' + data.result);
					}
				}
			});
		}
	});
	$('#register-form').submit(function(e) {
		submitRegisterForm();
	});
});

function submitRegisterForm() {
	var isReady = true;
	if(!$('#first-name').val()) {
		$('#first-name').closest('.form-group').addClass('has-error').addClass('has-feedback');
		$('#first-name').after('<span class="glyphicon glyphicon-remove form-control-feedback"></span>');
		$('#first-name').after('<p class="text-danger error-message">First Name can not be blank</p>');
		isReady = false;
	} 
	if(!$('#last-name').val()) {
		$('#last-name').closest('.form-group').addClass('has-error').addClass('has-feedback');
		$('#last-name').after('<span class="glyphicon glyphicon-remove form-control-feedback"></span>');
		$('#last-name').after('<p class="text-danger error-message">Last Name can not be blank</p>');
		isReady = false;
	} 
	if(!$('#email-register').val()) {
		$('#email-register').closest('.form-group').addClass('has-error').addClass('has-feedback');
		$('#email-register').after('<span class="glyphicon glyphicon-remove form-control-feedback"></span>');
		$('#email-register').after('<p class="text-danger error-message">Email can not be blank</p>');
		isReady = false;
	} 
	if(!$('#password').val()) {
		$('#password').closest('.form-group').addClass('has-error').addClass('has-feedback');
		$('#password').after('<span class="glyphicon glyphicon-remove form-control-feedback"></span>');
		$('#password').after('<p class="text-danger error-message">Password can not be blank</p>');
		isReady = false;
	}
	
	if(isReady === true) {
		$.ajax({
			type : 'POST',
			url : '/TagRecommend/register',
			data : {
				'firstName' : $('#first-name').val(),
				'lastName' : $('#last-name').val(),
				'email' : $('#email-register').val(),
				'password' : $('#password').val()
			}, success : function(data) {
				
			}
		});
	}
}