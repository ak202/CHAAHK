library(ggplot2)
library(rrepast)
library(plyr)
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

f <- AddFactor (name = "disturbanceRemovalChance", min = 0, max = .3)
f <- AddFactor (factors = f, name = "costPromotiveRes", min = 0, max = 1)
f <- AddFactor (factors = f, name = "costPromotiveIncRate", min = 0, max = .02)
f <- AddFactor (factors = f, name = "costDemotiveRes", min = 0, max = 1)
f <- AddFactor (factors = f, name = "costDemotiveIncRate", min = 0, max = .05)
f <- AddFactor (factors = f, name = "fecundityPromotiveRes", min = 0, max = 1)
f <- AddFactor (factors = f, name = "fecundityPromotiveIncRate", min = 0, max = .01)
f <- AddFactor (factors = f, name = "fecundityDemotiveRes", min = 0, max = 1)
f <- AddFactor (factors = f, name = "fecundityDemotiveIncRate", min = 0, max = .0025)
f <- AddFactor (factors = f, name = "uplandAmount", min = 0, max = 250)

runsims <- function(n) {
  params2 <- data.frame(1:n, params)[,-1]
  params2[,"randomSeed"] <- runif(n, min = 0, max = 9999999999)
  result <- RunExperiment(chaahk, 1, params2, objective)
  mayasim <- result$output[,2]
  output <- result$dataset
  parameters <- result$paramset
  data2 <- data.frame(mayasim, output, parameters)
  return(data2)
}

results <- lapply(seq(5,250,5), runsims)
setwd("/media/nvme/workspace2/Mayagrations/rrepast")
saveRDS(results,"stabilityresults.RDS")


setwd("/media/nvme/workspace2/Mayagrations/rrepast")
results <- readRDS("stabilityresults.RDS")

stability3 <- function(df) {
  cov.max <- sd(df$MaxL) / mean(df$MaxL)
  cov.min <- sd(df$MinL) / mean(df$MinL)
  cov.final <- sd(df$FinalL) / mean(df$FinalL)
  cov.maya <- sd(df$mayasim) / mean(df$mayasim)
  return(data.frame(cov.max, cov.min, cov.final, cov.maya))
}

results2 <- lapply(results, stability3)
results2 <- ldply(results2, data.frame)

stability.raw <- ggplot() +
  geom_line(aes(x=seq(5,250,5), cov.max), data=results2, color="green") +
  geom_line(aes(x=seq(5,250,5), cov.min),  data=results2, color="red") +
  geom_line(aes(x=seq(5,250,5), cov.final), data=results2,  color="blue") +
  scale_colour_manual(name = NULL, guide = guide_legend()) +
  theme(text = element_text(size=15), panel.background = element_rect(fill="grey")) +
  labs(x = "Number of Runs", y = "Coefficient of Variation")









summary(data2)
