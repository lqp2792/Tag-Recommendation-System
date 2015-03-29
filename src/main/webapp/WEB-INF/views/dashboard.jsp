<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta name="description" content="">
<meta name="author" content="">
<title>Social Bookmarking System</title>
<link href="css/bootstrap.min.css" rel="stylesheet">
<link href="css/metisMenu.min.css" rel="stylesheet">
<link href="css/sb-admin-2.css" rel="stylesheet">
<link href="css/panel.css" rel="stylesheet">
<link href="css/font-awesome.css" rel="stylesheet" type="text/css">
<script src="js/jquery.js"></script>
<script src="js/bootstrap.min.js"></script>
<script src="js/metisMenu.min.js"></script>
<script src="js/sb-admin-2.js"></script>
<script src="js/bootbox.js"></script>
<script src="js/panel.js"></script>
<!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
<!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
<!--[if lt IE 9]>
        <script src="js/html5shiv.js"></script>
        <script src="js/respond.min.js"></script>
        <![endif]-->
</head>
<body>
	<div id="wrapper">
		<!-- Navigation -->
		<nav class="navbar navbar-default navbar-static-top" role="navigation"
			style="margin-bottom: 0">
			<div class="navbar-header">
				<button type="button" class="navbar-toggle" data-toggle="collapse"
					data-target=".navbar-collapse">
					<span class="sr-only">Toggle navigation</span> <span class="icon-bar"></span> <span
						class="icon-bar"></span> <span class="icon-bar"></span>
				</button>
				<a class="navbar-brand" href="index.html">${firstName} ${lastName}</a>
			</div>
			<!-- /.navbar-header -->
			<ul class="nav navbar-top-links navbar-right">
				<li class="active" id="log-out"><a href="#">Log out</a></li>
			</ul>
			<!-- /.navbar-top-links -->
			<div class="navbar-default sidebar" role="navigation">
				<div class="sidebar-nav navbar-collapse">
					<ul class="nav" id="side-menu">
						<li class="sidebar-search">
							<div class="input-group custom-search-form">
								<input type="text" class="form-control" placeholder="Search..."> <span
									class="input-group-btn">
									<button class="btn btn-default" type="button">
										<i class="fa fa-search"></i>
									</button>
								</span>
							</div> <!-- /input-group -->
						</li>
						<li><a href="#"><i class="fa fa-files-o fa-fw"></i> My Links</a></li>
						<li><a href="#"><i class="fa fa-users fa-fw"></i> Network</a></li>
						<li><a href="#" id="discover-menu"><i class="fa fa-globe fa-fw"></i>
								Discover</a></li>
						<li><a href="#"><i class="fa fa-star-o fa-fw"></i> Trending</a></li>
						<li><a href="#" id="add-bookmark-menu" data-toggle="modal"
							data-target="#add-bookmark-modal"><i class="fa fa-plus-square fa-fw"></i> Add
								Bookmark</a></li>
						<li><a href="#"><i class="fa fa-cog fa-fw"></i> Setting</a></li>
					</ul>
				</div>
			</div>
		</nav>
		<!-- Page Content -->
		<div id="page-wrapper">
			<div class="container-fluid">
				<div class="row page-header">
					<div class="col-lg-2">
						<img src="images/user.png" class="img-thumbnail" alt="User Default Avatar"
							style="min-height: 100px; height: 100px;">
					</div>
					<div class="col-lg-10">
						<h2>${firstName} ${lastName}</h2>
						<h4 class="text-info">0 Bookmark 0 Following 0 Follower</h4>
					</div>
				</div>
				<div class="row page-content">
					<div class="col-lg-12">
						<div id="bookmarks">
							<c:if test="${not empty bookmarks}">
								<c:forEach var="bookmark" items="${bookmarks}">
									<div class="bookmark">
										<h3>
											<c:out value="${bookmark.title}" />
										</h3>
										<a class="text-info" href="<c:out value="${bookmark.url}"/>"> <c:out
												value="${bookmark.url}" />
										</a>
										<p>
											<i class="fa fa-tags"></i> Tags:
											<c:forEach var="tag" items="${bookmark.tags}">
												<span class="dropdown"><code class="bookmark-tag">
														#<c:out value="${tag}" />
													</code></span>
											</c:forEach>
										</p>
									</div>
								</c:forEach>
							</c:if>
						</div>
					</div>
				</div>
			</div>
		</div>
		<!-- Add Link Modal -->
		<div class="modal fade" id="add-bookmark-modal" tabindex="-1" role="dialog"
			aria-labelledby="addBookmarkModalLabel" aria-hidden="true">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal" aria-label="Close">
							<span aria-hidden="true">&times;</span>
						</button>
						<h4 class="modal-title text-center" id="addBookmarkModalLabel">
							<i class="fa fa-pencil"></i> Share New Link
						</h4>
					</div>
					<form:form method="post" role="form" modelAttribute="newBookmark"
						id="add-bookmark-form" action="dashboard/checkBookmark">
						<div class="modal-body">
							<div class="form-group">
								<form:label for="new-bookmark-textarea" class="control-label text-primary"
									path="url" id="new-bookmark-label">New Bookmark:</form:label>
								<form:textarea class="form-control" rows="5" id="new-bookmark-textarea"
									placeholder="Url Link" path="url" style="resize : none" />
							</div>
						</div>
						<div class="modal-footer">
							<button class="btn btn-success">
								<i class="fa fa-plus" id="add-bookmark-submit"></i> Add Bookmark
							</button>
						</div>
					</form:form>
				</div>
			</div>
		</div>
		<!-- Add Tag Modal -->
		<div class="modal fade" id="add-tag-modal" tabindex="-1" role="dialog"
			aria-hidden="true">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal" aria-label="Close">
							<span aria-hidden="true">&times;</span>
						</button>
						<h4 class="modal-title text-center">
							<i class="fa fa-tag"></i> Add Tag
						</h4>
					</div>
					<form method="post" role="form" id="add-tag-form" action="dashboard/addBookmark">
						<div class="modal-body">
							<div class="form-group">
								<div class="row bookmark-info">
									<div class="col-xs-3">
										<strong><p class="text-info">Url :</p></strong>
									</div>
									<div class="col-xs-9">
										<p id="bookmark-url"></p>
									</div>
								</div>
								<br />
								<div class="row bookmark-info">
									<div class="col-xs-3">
										<strong><p class="text-info">Title :</p></strong>
									</div>
									<div class="col-xs-9">
										<p id="bookmark-title"></p>
									</div>
								</div>
								<br />
								<div class="row bookmark-info">
									<div class="col-xs-3">
										<strong><p class="text-info">
												<i class="fa fa-tag"></i>Tags :
											</p></strong>
									</div>
									<div class="col-xs-9">
										<textarea class="form-control" id="bookmark-tags" rows="3" placeholder="Tags"
											style="resize: none" data-toggle="tooltip" data-placement="right"
											data-original-title="Tag are important part of organizing on Social Bookmarking Service. Using tags that are smart and simple benifits both you and community"></textarea>
										<p class="text-info">
											Recommend Tags <i class="fa fa-question" data-toggle="tooltip"
												data-placement="right"
												data-original-title="System use Topic Modeling to recommend tags to user"></i>
										</p>
										<div class="waiting-div"></div>
										<div id="recommend-tags-div"></div>
									</div>
								</div>
								<br />
								<div class="row bookmark-info">
									<div class="form-group">
										<div class="col-xs-3">
											<strong><p class="text-info">
													<i class="fa fa-comment"></i> Comment :
												</p></strong>
										</div>
										<div class="col-xs-9">
											<textarea class="form-control" id="bookmark-comment" rows="4"
												placeholder="Your Comment" style="resize: none" data-toggle="tooltip"
												data-placement="right"
												data-original-title="What are you thinking about this Bookmark?"></textarea>
										</div>
									</div>
								</div>
							</div>
						</div>
						<div class="modal-footer">
							<button class="btn btn-success">
								<i class="fa fa-plus"></i> Add Bookmark
							</button>
						</div>
					</form>
				</div>
			</div>
		</div>
	</div>
</body>
</html>