library(ggplot2)
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
f <- AddFactor (factors = f, name = "uplandAmount", min = 0, max = 250)

oat <- function(row,n) {
  params2 <- GetSimulationParameters(chaahk)
  name <- f[row,2]
  min <- as.numeric(f[row,3])
  max <- as.numeric(f[row,4])
  range <- data.frame(seq(min, max, (max - min)/(n-1)))
  temp.oat <- data.frame(1:n, params)[,-1]
  temp.oat[,name] <- range
  temp.oat[,"randomSeed"] <- runif(n, min = 0, max = 9999999999)
  result <- RunExperiment(chaahk, 1, temp.oat, objective)
  mayasim <- result$output[,2]
  output <- result$dataset
  parameters <- result$paramset
  data2 <- data.frame(mayasim, output, parameters)
  return(data2)
}

chea <- lapply(c(1:10), oat, n=150)

makePlots <- function(row) {
  cols <- c("Maximum Population"="green","Minimum Population"="red", "Final Population"="blue")
  p <- ggplot() + 
    geom_smooth(aes(chea[[row]][,f[row,2]], chea[[row]]$MaxL, color="Maximum Population"), se=FALSE) +
    geom_point(aes(chea[[row]][,f[row,2]], chea[[row]]$MaxL, color="Maximum Population"), shape=3) +
    geom_smooth(aes(chea[[row]][,f[row,2]], chea[[row]]$MinL, color="Minimum Population"), se=FALSE) +
    geom_point(aes(chea[[row]][,f[row,2]], chea[[row]]$MinL, color="Minimum Population"), shape=4) +
    geom_smooth(aes(chea[[row]][,f[row,2]], chea[[row]]$FinalL, color="Final Population"), se=FALSE) +
    geom_point(aes(chea[[row]][,f[row,2]], chea[[row]]$FinalL, color="Final Population"), shape=1) +
    labs(title = paste(f[row,2], "Effect on Population Dynamics"), x = f[row,2], y = "Value") +
    scale_colour_manual(name = NULL, values=cols, guide = guide_legend()) +
    theme(text = element_text(size=20), panel.background = element_rect(fill="grey"))
    ggsave(paste("/home/akara/Dropbox/thesis/figures/rresults/dotplots/",f[row,2],".png",sep=""),p)
  return(p)
}

chea2 <- lapply(c(1:10), makePlots)
