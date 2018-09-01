library(rrepast) 

dir <- "/media/nvme/Mayagrations/"
Easy.Setup(dir)
chaahk <- Model(modeldir=dir, dataset="final",1650, TRUE)
params <- GetSimulationParameters(chaahk)

objective <- function(p, r) {
  criteria <- c()
  score <- (r$MinL + r$FinalL + 100)/r$MaxL
  criteria <- cbind(score)
  return(score)
}

install.packages("scatterplot3d")

f <- AddFactor (name = "disturbanceRemovalChance", min = 0, max = .3)
f <- AddFactor (factors = f, name = "fecundityDemotiveRes", min = 0, max = 1)

ff <- AoE.FullFactorial(400, f)
ff2 <- BuildParameterSet(ff, params)

result <- RunExperiment(chaahk, 1, ff2, objective)

mayasim <- result$output[,2]
output <- result$dataset
parameters <- result$paramset
data <- data.frame(mayasim, output, parameters)
saveRDS(data, "UrcFdr.rds")
scatter3D(data$disturbanceRemovalChance, data$fecundityDemotiveRes, data$FinalL, ticktype="detailed")
plotrgl()




library(scatterplot3d)
x <- c(rep(1, 5),rep(2, 5),rep(3, 5),rep(4, 5),rep(5, 5))
y <- c(1:5, 1:5, 1:5, 1:5, 1:5)
z <- c(rep(1, 5),rep(2, 5),rep(3, 5),rep(2, 5),rep(1, 5))
df <- df(x, y, z)
scatterplot3d(x, y, z)
scatterplot3d(df)

install.packages("plot3D")
library(plot3D)
surf3D(mesh)
scatter3D(x, y, z)
plotrgl()
hist3D(x, y, z)

mesh <- mesh(x, y, z)
install.packages("plot3Drgl")
library(plot3Drgl)
library(spatial)

(10:1)

x <- c(rep(1, 5),rep(2, 5),rep(3, 5),rep(4, 5),rep(5, 5))
y <- c(1:5, 1:5, 1:5, 1:5, 1:5)
z <- c(rep(1, 5),rep(2, 5),rep(3, 5),rep(2, 5),rep(1, 5))
w <- matrix(z, nrow=5, ncol=5)


df <- df(x, y, z)
surf <- surf.ls(2, x, y, z)
surf3D(as.matrix(surf$f))
surf$f
class(surf$f)

plot(surf)
persp3D(z=z)

