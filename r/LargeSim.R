library(rrepast) 
dir <- "/media/nvme/Mayagrations/"
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
f <- AddFactor (factors = f, name = "costPromotiveIncRate", min = 0, max = .2)
f <- AddFactor (factors = f, name = "costDemotiveRes", min = 0, max = 1)
f <- AddFactor (factors = f, name = "costDemotiveIncRate", min = 0, max = .2)
f <- AddFactor (factors = f, name = "fecundityPromotiveRes", min = 0, max = 1)
f <- AddFactor (factors = f, name = "fecundityPromotiveIncRate", min = 0, max = .01)
f <- AddFactor (factors = f, name = "fecundityDemotiveRes;   ", min = 0, max = 1)
f <- AddFactor (factors = f, name = "fecundityDemotiveIncRate", min = 0, max = .005)
f



lhc <- AoE.LatinHypercube(10000, f)
lhc2 <- BuildParameterSet(lhc, params)

result <- RunExperiment(chaahk, 1, lhc2, objective)

setwd("/media/nvme/workspace2/Mayagrations/rrepast")
saveRDS(result, "result.RDS")
Sys.time()


mayasim <- result$output[,2]
output <- result$dataset
params <- result$paramset

data <- data.frame(mayasim, output, params)
colnames(data)[37] <- "fecundityDemotiveRes"



library(rpart)
library(rpart.plot)

result <- rpart(mayasim ~ disturbanceRemovalChance + 
                  costPromotiveRes              +                 
                  costPromotiveIncRate          +                 
                  costDemotiveRes               +    
                  costDemotiveIncRate           +      
                  fecundityPromotiveRes         +       
                  fecundityPromotiveIncRate     +           
                  fecundityDemotiveRes          +          
                  fecundityDemotiveIncRate, data = data)          
rpart.plot(result)


name <- "costPromotiveRes"
str(f)

params$costPromotiveRes
params


oat <- function(row) {
  name <- f[row,2]
  min <- as.numeric(f[row,3])
  max <- as.numeric(f[row,4])
  f2 <- seq(min, max, (max - min)/5)
  oat.params <- BuildParameterSet(f2, params)
  result <- RunExperiment(chaahk, 1, oat.params, objective)
  mayasim <- result$output[,2]
  output <- result$dataset
  params <- result$paramset
  data <- data.frame(mayasim, output, params)
  return(data)
}

chea <- lapply(1:nrow(f), oat)
str(chea)
now <- chea[[1]]
str(now)
library(ggplot2)

qplot(data[,"fecundityPromotiveIncRate"], data$mayasim)

a <- 2
eval(a)











library(ggplot2)









params2 <- data.frame(params)
params2 <- t(params2)
params2 <- params2[ order(row.names(params2)), ]
params2 <- data.frame(params2)




params2



# 

# 
# f <- AddFactor(name="deathChance", min=0, max=.3)
# f <- AddFactor(f, name="costDemotiveMax",min=0, max=20)
# f <- AddFactor(f, name="costPromotiveRes", min=1, max=1)
# 
# v <- Easy.Stability("/media/nvme/Mayagrations/", "final", 1650, f, FUN=cal)
# v <- Easy.Morris()
# v <- Easy.Sobol(dir, final, 1650, f, exp.n=100, exp.r=1, FUN=cal)
# 
# class(v)
# 
# 
# 

# library(ggplot2)
# 
# points <- matrix(c(c(6,2,3), c(3,5,1)), ncol=2)
# colnames(points) <- c("x", "y")
# rownames(points) <- c(1,2,3)
# points <- data.frame(points)
# 
# lines1 <- matrix(c(1,2,2,3,3,1,5,6,10),ncol=3)
# 
# p <- ggplot(data, aes(x,y))
# p <- p + geom_point()
# 
# connect1 <- function(input){
#   x1 <- points[input[1],1]
#   y1 <- points[input[1],2]
#   x2 <- points[input[2],1]
#   y2 <- points[input[2],2]
#   return(c(x1,y1,x2,y2,input[3]))
# }
# 
# lines2 <- data.frame(t(apply(lines1,1,connect1)))
# colnames(lines2) <- c("x1", "y1", "x2", "y2", "weight")
# p <- p +  geom_segment(aes(x=x1, y=y1, xend=x2, yend=y2,
#                            colour = weight),data=lines2)
# p
# 
