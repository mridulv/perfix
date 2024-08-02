# Use an existing image as a base
FROM node:latest

# Set the working directory inside the container
WORKDIR /usr/src/app

# Copy package.json and package-lock.json files to the working directory
COPY package*.json ./

# Install npm dependencies
RUN yarn install

# Copy all project files to the working directory
COPY . .

RUN yarn run build

# Expose the port your app runs on
EXPOSE 3000

# Command to run your app (change this according to your project)
CMD ["yarn", "dev"]
