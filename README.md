# Jenkins Testcafe Plugin
[![GitHub release](https://img.shields.io/github/v/release/wentwrong/testcafe-jenkins-plugin?include_prereleases)](https://github.com/wentwrong/testcafe-jenkins-plugin/releases)

This plugin integrates <a href="https://devexpress.github.io/testcafe/">Testcafe</a> with Jenkins allowing to view attachments (screenshots and videos) directly on test page.

## How-to
This plugin should be used with [Testcafe Jenkins Reporter](https://github.com/wentwrong/testcafe-reporter-jenkins/).
```
npm i testcafe-reporter-jenkins
```
### Usage on freestyle jobs
Choose "Publish JUnit test result report" at Post-build Actions and "Testcafe Report" as additional test report features

![freestyleJob](https://user-images.githubusercontent.com/26363017/81811616-95f2b680-952d-11ea-8a84-cf555ae5fd50.png)

### Usage on Pipeline
1. Run testcafe with Testcafe Jenkins Reporter specified
```
testcafe chrome e2e/**/* -r jenkins:report.xml
```

2. Collect generated report via junit
```
junit keepLongStdio: true, testDataPublishers: [[$class: 'TestcafePublisher']], testResults: 'report.xml'
```

**Notice that keepLongStdio param should be set to true, otherwise test system-out will be truncated by junit**

You may look at [example](https://github.com/wentwrong/experiments-with-jenkins) of running tests in parallel distributed among multiple agents.

## Known issues
> **When trying to watch a video, the player is either blank or won't play. How do I fix this?**

Most likely the problem is caused by Content Security Policy in Jenkins. You can check browser console to see what policies exactly were violated. The most common solution is
**Manage Jenkins**→**Script Console** →
```java
System.setProperty("hudson.model.DirectoryBrowserSupport.CSP", "media-src 'self';")
```
