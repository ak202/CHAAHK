library(rpart)
setwd("/home/akara/workspace9/Mayagrations/r")
params <- read.csv("params1.csv")
out <- read.csv("out1.csv")

data <- merge(params, out)
data$collapse1 <- (data$min+1)/(data$max+1)
data$collapse2 <- (data$final+1)/(data$max+1)

model1 <- rpart(collapse2 ~ weightRegen + foodUnsustainability + weightResil
+ weightBloatRate + foodRegen + foodDecay + weightDrought + foodResil
+ foodDrought, + foodDrought, data=data, method="anova")
plot(model1, uniform=TRUE)
text(model1, all=TRUE)
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
