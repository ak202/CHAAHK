# TODO: Add comment
# 
# Author: akara
###############################################################################

out1 <- read.csv('output1.csv')
params1 <- read.csv('params1.csv')
out2 <- read.csv('output2.csv')
params2 <- read.csv('params2.csv')
out3 <- read.csv('output3.csv')
params3 <- read.csv('params3.csv')
out4 <- read.csv('output4.csv')
params4 <- read.csv('params4.csv')

out <- rbind(out1, out2, out3, out4)
params <- rbind(params1, params2, params3, params4)
params <- params[,c(3,4,5,6)]
out <- out[,2:4]
data <- data.frame(params, out)
res1 <- data[,6]/data[,5]
res2 <- data[,7]/data[,5]
data[,6:7] <- NULL
data <- data.frame(data, res1, res2)
write.csv(data, 'data3.csv')

