library(rpart)
setwd("/home/akara/workspace/Mayagrations/output/")
params <- read.csv("params2.csv")
out <- read.csv("out2.csv")
data <- merge(params, out)
data$collapse1 <- (data$MinPop+1)/(data$MaxPop+1)
data$collapse2 <- (data$countPop+1)/(data$MaxPop+1)

data <- rbind(data,data)

model1 <- rpart(collapse2~fecundityResil+costResil+fecundityDisturbance,data=data,method="anova")
plot(margin=0, compress=)
text(model1, all=TRUE)
post(model1, 'test', 'test.ps')
summary(model1)
data <- params
data$pop1 <- NA
data$pop2 <- NA
for (run in 1:nrow(params)) {
  data$pop1[run] <- out[out$run==run&out$tick==9999,3]
  data$pop2[run] <- out[out$run==run&out$tick==16499,3]
}
  
summary(data)
out[out$run==7&out$tick==9999,3]


data$pop2[7]
