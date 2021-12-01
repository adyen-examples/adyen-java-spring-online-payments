output "app_version" {
  value = "${aws_elastic_beanstalk_application_version.default.name}"
}
output "env_name" {
  value = "${module.elastic_beanstalk_environment.name}"
}