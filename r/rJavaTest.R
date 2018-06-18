library(rrepast) 
# setwd("/media/nvme/workspace2/Mayagrations/r/")
Easy.Setup("/media/nvme/Mayagrations/")
dir <- "/media/nvme/Mayagrations/"
final <- "final"
obj <- Model(modeldir=install.dir, dataset="final",1650, TRUE)
Run(obj)
output <- GetResults()
output
class(output)

GetSimulationParameters(obj)

cal <- function(p, r) {
  criteria <- c()
  score <- r$MinL/r$MaxL
  criteria <- cbind(score)
  return(score)
}

f <- AddFactor(name="deathChance", min=0, max=.3)
f <- AddFactor(f, name="costDemotiveMax",min=0, max=20)
f <- AddFactor(f, name="costPromotiveRes", min=1, max=1)

v <- Easy.Stability("/media/nvme/Mayagrations/", "final", 1650, f, FUN=cal)
v <- Easy.Morris()
v <- Easy.Sobol(dir, final, 1650, f, exp.n=100, exp.r=1, FUN=cal)

class(v)




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
