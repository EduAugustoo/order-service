custom_build(
    ref = 'order-service',
    command = '.\\mvnw spring-boot:build-image -Dspring-boot.build-image.imageName=${env.EXPECTED_REF}',
    deps = ['pom.xml', 'src']
)

k8s_yaml(kustomize('k8s'))

k8s_resource('order-service', port_forwards=['9002'])