var DepartmentSchema = new mongoose.Schema({
    Name: String,
    head:String,
    courses: Array,
    teachers: Array
});
