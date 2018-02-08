# TODO: Add comment
# 
# Author: akara
###############################################################################
library(rpart)
data <- read.csv('data2.csv',row.names=1)

model1 <- rpart(res1 ~ canalDeath + canalDecay + sust + drought, data)
model2 <- rpart(res2 ~ canalDeath + canalDecay + sust + drought, data)
model3 <- rpart(MaxPop ~ canalDeath + canalDecay + sust + drought, data)

summary(model1)
summary(model2)

plot(model1)
text(model1, all=TRUE)

plot(model2)
text(model2, all=TRUE)

plot(model3)
text(model3, all=TRUE)




a <- meanvar(model1)

hist(data[,5])
library(vegan)
data2 <- data[data[,5]<50,]
summary(data2)