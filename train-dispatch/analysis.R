library("ggplot2", lib.loc="/usr/local/lib/R/3.2/site-library")
results <- read.csv("~/Desktop/c.csv")

imp_cost <- results$imp_cost
base_cost <- results$base_cost
imp_cost_diff = imp_cost - results$min_cost
base_cost_diff = base_cost - results$min_cost

###############################################################################
# total costs

mean(imp_cost)  # imp average
sd(imp_cost)  # imp stdev
mean(base_cost)  # base average
sd(base_cost)  # base stdev

# Histogram
# hist(imp_cost)
# hist(base_cost)

# bar
barplot(c(mean(imp_cost),mean(base_cost)), names.arg = c("improved","base"), main = "Total Cost")

###############################################################################
# additional costs with min costs substracted

mean(imp_cost_diff)  # imp average
sd(imp_cost_diff)  # imp stdev
mean(base_cost_diff)  # base average
sd(base_cost_diff)  # base stdev

# Histograms
# hist(imp_cost_diff)
# hist(base_cost_diff)

# bar
barplot(c(mean(imp_cost_diff),mean(base_cost_diff)), names.arg = c("improved","base"), main = "Mileage Cost Subtracted (Delay Cost Only)")

###############################################################################
# PDF estimation w/ kernel density estimation

imp_diff <- data.frame(additional_cost=(imp_cost - results$min_cost))
base_diff <- data.frame(additional_cost=(base_cost - results$min_cost))

# add name coloumn
imp_diff$strategy <- 'improved'
base_diff$strategy <- 'baseline'
cost_diffs <- rbind(imp_diff, base_diff)
ggplot(cost_diffs, aes(additional_cost, fill=strategy)) + geom_density(alpha = 0.2)
