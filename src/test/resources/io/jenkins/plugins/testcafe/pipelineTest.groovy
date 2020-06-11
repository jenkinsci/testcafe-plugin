node {
    junit testResults: "*.xml", keepLongStdio: true, testDataPublishers: [[$class: 'TestCafePublisher']]
}
