library(rrepast) 

dir <- "/media/nvme/Chaahk/"
Easy.Setup(dir)
chaahk <- Model(modeldir=dir, dataset="final",1650, TRUE)
params <- GetSimulationParameters(chaahk)

objective <- function(p, r) {
  criteria <- c()
  score <- (r$MinL + r$FinalL + 100)/r$MaxL
  criteria <- cbind(score)
  return(score)
}

f <- AddFactor (name = "disturbanceRemovalChance", min = 0, max = .3)
f <- AddFactor (factors = f, name = "costPromotiveRes", min = 0, max = 1)
f <- AddFactor (factors = f, name = "costPromotiveIncRate", min = 0, max = .02)
f <- AddFactor (factors = f, name = "costDemotiveRes", min = 0, max = 1)
f <- AddFactor (factors = f, name = "costDemotiveIncRate", min = 0, max = .05)
f <- AddFactor (factors = f, name = "fecundityPromotiveRes", min = 0, max = 1)
f <- AddFactor (factors = f, name = "fecundityPromotiveIncRate", min = 0, max = .01)
f <- AddFactor (factors = f, name = "fecundityDemotiveRes", min = 0, max = 1)
f <- AddFactor (factors = f, name = "fecundityDemotiveIncRate", min = 0, max = .0025)

a <- Easy.Sobol()