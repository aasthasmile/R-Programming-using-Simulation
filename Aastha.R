#Aastha Jain
#Excercise 1
craps <-function(){
  firstRoll <-TRUE
  result <-2 #intializing it with value other than 1(win) or 0(loss)
  
  repeat{
    
    if(firstRoll== TRUE){
      sum<-sample(1:6,1)+sample(1:6,1)
      
      #print(paste0("sum is ",sum))
      
      if(sum %in% c(7,11)){
        print(1)
        #print("firstroll wins")
        break
      }
      else if(sum %in% c(2,3,12)){
        print(0)
        #print("firstroll losses")
        break
      }
      
      firstRoll=FALSE
    }
    
    else if(firstRoll== FALSE){
      sum<-sample(1:6,1)+sample(1:6,1)
      #print(paste0("sum is ",sum))
      
      if(sum %in% c(7)){
        #print("secondRoll losses")
        result <-0
        print(0)
        
      }
      else if(sum %in% c(4,5,6,8,9,10)){
        #print("secondRoll wins")
        result <-1
        print(1)
      }
      
    }
    
    if(result== 1 || result==0) break
  }
}

f <-function(){
  return(sample(0:1,1))
}

estimate_bernoulli <- function(f,delta,epsilon){
  
  z <-qnorm((1+delta)/2)
  x = vector(mode="numeric", length=10000)
  set<-vector(mode="numeric",0)
  tsum=0
  n=10000
  inc=10000
  
  repeat
  {
    sum = 0
    sumsq = 0
    for (i in 1:n) {
      x[i] = f()
      sum = sum + x[i]
      sumsq = sumsq + x[i]*x[i]
    }
    tsum=tsum+sum
    
    #appending new samples to old samples
    set<-c(set,x)
    
    lambda = tsum/n
    sigmasq = (sumsq - (lambda*lambda*n))/(n-1)
    se = sqrt(var(set)/n)
    
    print(paste0("z*se=",(z*se)))
    
    if((z*se) <= epsilon)
      {
        print(lambda)
    }
    print(n)
    n<-n+inc
    if(z*se <= epsilon) break
  }
 
}

#<-function(craps,delta,epsilon){
  #print(paste0("The probability of winning in a games of craps is",estimate_bernoulli(craps(),0.90,0.005)));
#}

network_reliability <-function(){
  
  #prob of not breaking edge
  x=vector(mode="numeric",length=7)
  p=vector(mode="numeric",length=7)
  
  noOfBroken <-0
  lambda<-0
  noofSamples<-0
  variance<-0
  
  repeat{
    
  for(i in 1:7){
    x[i]=f()
    if(x[i]==1){
      p[i]=0.999
    }
    else if(x[i]==0){
      p[i]=0.001
    }
 }
 
  pbar<-1
 
  noofSamples<- noofSamples+1
  #print(paste0("noofSamples:",noofSamples))
  
  if(x[1]==0 && x[2]==0 || x[5]==0 && x[6]==0 && x[7]==0){
    noOfBroken <- noOfBroken +1
    for(i in 1:length(p)){
      pbar <- pbar*p[i]
    }
    #print(x)
    #print(p)
    #print(pbar)
    lambda <-lambda +pbar
  }
  #print(paste0("noOfbroken: ",noOfBroken))
  if(noOfBroken == 50) break
  }
  print(lambda)
  
  #variance is e[x^2]-E^2[X]
  variance=50*lambda -(lambda^2) 
  print(variance)
  
  #number of samples n needed to reach 50 broken con???gurations
  print(noofSamples)
}