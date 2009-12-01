require 'buildr/scala'

repositories.remote = [ "http://mirrors.ibiblio.org/pub/mirrors/maven2/" ]
repositories.remote << "http://scala-tools.org/repo-releases/"

H2 =  "com.h2database:h2:jar:1.0.76"
SLF4J = [ "org.slf4j:slf4j-api:jar:1.5.2",
          "org.slf4j:slf4j-simple:jar:1.5.2",
          "ch.qos.logback:logback-core:jar:0.9.9" ]

define "srecord", :version => "0.1", :group => "srecord" do
  compile.using :scalac
  compile.with SLF4J
  test.with [H2]
  package :jar
  #test.using :junit
end

