library(rrepast)
library(rJava)
install.dir <- "/home/akara/Mayagrations/"

jvm.init()
library(rJava)
debug(Model)
obj <- Model(modeldir=install.dir, dataset="dataset",TRUE)

configModelDirs(install.dir)
jvm.init()
e <- Engine()

config.check(install.dir)
check.integration(install.dir)
check.scenario(install.dir)
debug(Engine)
Engine()
ShowClassPath()
Engine()
undebug(Engine)
Engine()
o <- new(J("java.lang.String"),"Hello World")
test <- new(J(engineclazz()))
test <- new(J("rrepast-engine.org.haldane.rrepast.RepastEngine"))
print(o)

s <- .jnew("java/lang/String", "Hello World!")

.jinit()
.jclasspath
.jclassPath()

test <- new(J("org.haldane.rrepast.RepastEngine"))
