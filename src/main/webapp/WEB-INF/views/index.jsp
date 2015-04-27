<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta name="description" content="">
<meta name="author" content="">
<title>Social Bookmarking System</title>
<link href="<c:url value="css/bootstrap.min.css" />" rel="stylesheet">
<link href="<c:url value="css/main.css"/>" rel="stylesheet">
<link href="<c:url value="css/font-awesome.css"/>" rel="stylesheet" type="text/css">
<script src="<c:url value="js/jquery.js"/>"></script>
<script src="<c:url value="js/bootstrap.min.js"/>"></script>
<script src="<c:url value="js/main.js"/>"></script>
</head>
<body>
	<header id="header" role="banner">
		<div class="navbar-header">
			<button type="button" class="navbar-toggle collapsed">
				<span class="sr-only">Toggle navigation</span> <span class="icon-bar"></span> <span
					class="icon-bar"></span> <span class="icon-bar"></span>
			</button>
		</div>
		<div class="container">
			<div id="navbar" class="navbar navbar-default ">
				<div class="collapse navbar-collapse">
					<ul class="nav navbar-nav">
						<li><a href="#login" data-toggle="modal" data-target="#login-modal">Log In</a></li>
						<li><a href="#register" id="register-a" data-toggle="modal"
							data-target="#register-modal">Register</a></li>
					</ul>
				</div>
			</div>
		</div>
	</header>
	<!--/#header-->
	<section id="main-slider" class="carousel">
		<div class="carousel-inner">
			<div class="item active">
				<div class="container">
					<div class="carousel-content">
						<h1>Social Bookmarking Service</h1>
						<p class="lead">
							A centralized online service which enables users to add, annotate, edit <br />and
							share bookmarks of web documents
						</p>
					</div>
				</div>
			</div>
			<!--/.item-->
			<div class="item">
				<div class="container">
					<div class="carousel-content">
						<h1>Tag Recommendation</h1>
						<p class="lead">
							The task of predicting folksonomy tags for a given user and item, based on past
							user behavior <br />and possibly other information
						</p>
					</div>
				</div>
			</div>
			<!--/.item-->
		</div>
		<!--/.carousel-inner-->
		<a class="prev" href="#main-slider" data-slide="prev"><i class="icon-angle-left"></i></a>
		<a class="next" href="#main-slider" data-slide="next"><i class="icon-angle-right"></i></a>
	</section>
	<!-- Login Modal -->
	<div class="modal fade" id="login-modal" tabindex="-1" role="dialog"
		aria-labelledby="loginModalLabel" aria-hidden="true">
		<div class="modal-dialog modal-sm">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<h4 class="modal-title text-center" id="loginModalLabel">Log In</h4>
					<div class="text-danger text-center error-message"></div>
				</div>
				<form action="login" method="post" role="form" id="login-form">
					<div class="modal-body">
						<div class="form-group">
							<label for="login-email" class="control-label">Email: </label> <input type="text"
								class="form-control input-sm " id="login-email" placeholder="Email" />
								<div class="error-message"></div>
						</div>
						<div class="form-group">
							<label for="login-password" class="control-label">Password: </label> <input
								type="password" class="
								form-control input-sm" id="login-password"
								placeholder="Your Password" />
								<div class="error-message"></div>
						</div>
					</div>
					<div class="modal-footer">
						<button type="submit" class="btn btn-info btn-block">Login</button>
					</div>
				</form>
			</div>
		</div>
	</div>
	<!-- Register Modal -->
	<div class="modal fade" id="register-modal" tabindex="-1" role="dialog"
		aria-labelledby="registerModalLabel" aria-hidden="true">
		<div class="modal-dialog modal-sm">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<h4 class="modal-title text-center" id="registerModalLabel">Register</h4>
				</div>
				<!-- Start Register Form -->
				<form action="register" method="post" role="form" id="register-form">
					<div class="modal-body">
						<div class="row">
							<div class="col-xs-6 col-sm-6 col-md-6">
								<div class="form-group">
									<label for="first-name">First Name: </label> <input type="text"
										class="form-control input-sm" id="first-name" placeholder="First Name" />
								</div>
							</div>
							<div class="col-xs-6 col-sm-6 col-md-6">
								<div class="form-group">
									<label for="last-name" class="control-label">Last Name: </label> <input
										type="text" class="form-control input-sm" id="last-name" placeholder="Last Name"/>
									<div class="error-message"></div>
								</div>
							</div>
						</div>
						<div class="form-group">
							<label for="email-register">Email: </label> <input type="text"
								class="form-control input-sm" id="email-register" placeholder="Your Email" />
							<div class="error-message"></div>
						</div>
						<div class="form-group">
							<label for="password">Password: </label> <input type="password"
								class="form-control input-sm" id="password" placeholder="Your Password" />
						</div>
					</div>
					<div class="modal-footer">
						<button type="submit" class="btn btn-info btn-block">Register</button>
					</div>
				</form>
			</div>
		</div>
	</div>
</body>
</html>