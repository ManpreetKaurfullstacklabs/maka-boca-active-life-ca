provider "kubernetes" {
  config_path = "~/.kube/config"
}

provider "helm" {
  kubernetes {
    config_path = "~/.kube/config"
  }
}

resource "helm_release" "active_life" {
  name       = "springboot-app"
  chart = "/Users/Manpreet.Kaur/Source/SpringBoot-Project/maka-boca-active-life-ca/springboot-chart"

  namespace  = "manpreetkaurfullstac-dev"

  set {
    name  = "image.repository"
    value = var.image_repo
  }

  set {
    name  = "image.tag"
    value = var.image_tag
  }

  set {
    name  = "image.pullPolicy"
    value = "Always"
  }

  set {
    name  = "imagePullSecrets[0].name"
    value = "ghcr-pull-secret"
  }

  set {
    name  = "service.port"
    value = 8080
  }
}

