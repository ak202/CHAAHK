library(ggplot2)

data <- readRDS("data.RDS")

data2 <- c(data$MaxL, data$MinL, data$FinalL)
factors <- factor(c(rep("max", 20000), rep("min", 20000), rep("final", 20000)))
data2 <- data.frame(data2, factors)
colnames(data2) <- c("value", "factor")

boxs <- ggplot() + geom_boxplot(aes(y=value, x=factor), data=data2, size=1.3) +
  scale_y_continuous(breaks = c(0,5,10,35,100,350,1000,2000,3000), trans = "log") +
  labs(x = "Census Description (log scale)", y = "Number of Groups") +
  scale_x_discrete(labels = c('Final Count','Maximum Groups','Post-Collapse')) +
  theme(text = element_text(size=20), panel.background = element_rect(fill="grey"))

histogram <- ggplot() + 
  geom_histogram(aes(mayasim), data=data, binwidth=.25, fill="orange", linetype=1, color="black") +
  labs(x = "Divergence from Maya", y = "Count") +
  theme(text = element_text(size=20))



