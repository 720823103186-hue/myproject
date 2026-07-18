let taskList = document.getElementById("taskList");

window.onload = function(){
    showTasks();
}

function addTask(){

    let input = document.getElementById("taskInput");

    if(input.value.trim()==""){
        alert("Please enter a task");
        return;
    }

    let tasks = JSON.parse(localStorage.getItem("tasks")) || [];

    tasks.push({
        text:input.value,
        done:false
    });

    localStorage.setItem("tasks",JSON.stringify(tasks));

    input.value="";

    showTasks();
}

function showTasks(){

    taskList.innerHTML="";

    let tasks = JSON.parse(localStorage.getItem("tasks")) || [];

    tasks.forEach((task,index)=>{

        let li=document.createElement("li");

        if(task.done){
            li.classList.add("completed");
        }

        let span=document.createElement("span");
        span.className="task-text";
        span.innerText=task.text;

        span.onclick=function(){
            toggleTask(index);
        }

        let del=document.createElement("button");
        del.innerText="Delete";
        del.className="delete";

        del.onclick=function(){
            deleteTask(index);
        }

        li.appendChild(span);
        li.appendChild(del);

        taskList.appendChild(li);

    });

}

function toggleTask(index){

    let tasks = JSON.parse(localStorage.getItem("tasks"));

    tasks[index].done=!tasks[index].done;

    localStorage.setItem("tasks",JSON.stringify(tasks));

    showTasks();

}

function deleteTask(index){

    let tasks = JSON.parse(localStorage.getItem("tasks"));

    tasks.splice(index,1);

    localStorage.setItem("tasks",JSON.stringify(tasks));

    showTasks();

}