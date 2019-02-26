setwd("/media/sdd/workspace2/Chaahk/output/")
params <- read.csv("params1.csv")
out <- read.csv("out1.csv")
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
