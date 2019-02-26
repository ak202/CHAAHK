library(rrepast) 
library(ggplot2)
dir <- "/media/nvme/Chaahk/"
Easy.Setup(dir)
chaahk <- Model(modeldir=dir, dataset="final",1650, TRUE)
params <- GetSimulationParameters(chaahk)

getseed <- function(){
  return(runif(1, min = 0, max = 9999999999))
}

pview <- function(p) {
  require(htmltools)
  p <- p[ , order(colnames(p))]
  p <- t(p)
  p <- data.frame(p)
  html_print(pre(paste0(capture.output(print(p)), collapse="\n")))
}

objective <- function(p, r) {
  criteria <- c()
  score <- (r$MinL + r$FinalL + 100)/r$MaxL
  criteria <- cbind(score)
  return(score)
}

params$disturbanceRemovalChance <- 0
params$costPromotiveRes <- 0
params$costDemotiveRes <- 1

f <- AddFactor (name = "fecundityDemotiveExponent", min = 1, max = 10)
f <- AddFactor (factors = f, name = "fecundityDemotiveThreshold", min = 1, max = 10)

lhc <- AoE.LatinHypercube(5000, f)
lhc2 <- BuildParameterSet(lhc, params)




result <- RunExperiment(chaahk, 1, lhc2, objective)

mayasim <- result$output[,2]
output <- result$dataset
parameters <- result$paramset
data <- data.frame(mayasim, output, parameters)

library(plot3D)
library(plot3Drgl)

length(expo)
length(thres)
length(final)

summary(expo)
summary(thres)
summary(final)


expo <- data$fecundityDemotiveExponent
thres <- data$fecundityDemotiveThreshold
final <- as.numeric(data$FinalL)

scatter3D(expo, thres, final)
plotrgl()


