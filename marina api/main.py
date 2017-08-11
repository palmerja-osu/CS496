from google.appengine.ext import ndb
from datetime import datetime
import webapp2
import json

#define boat class
class Boat(ndb.Model):
	id = ndb.StringProperty()
	name = ndb.StringProperty(required=True)
	type = ndb.StringProperty()
	length = ndb.IntegerProperty()
	at_sea = ndb.BooleanProperty()

#manage boat verbs
class BoatHandler(webapp2.RequestHandler):
	#add new boat
	def post(self):
		boat_data = json.loads(self.request.body)
		new_boat = Boat(name=boat_data['name'], at_sea=True)
		if boat_data.get('type'):
			new_boat.type = boat_data['type']
		if boat_data.get('length'):
			new_boat.length = boat_data['length']
		new_boat.put()
		new_boat.id = new_boat.key.urlsafe()
		new_boat.put()
		boat_dict = new_boat.to_dict()
		boat_dict['self'] = '/boat/' + new_boat.id
		self.response.write(json.dumps(boat_dict))

	#get boat info
	def get(self, id=None):
		if id:
			curr_boat = ndb.Key(urlsafe=id).get()
			curr_boat_dict = curr_boat.to_dict()
			curr_boat_dict['self'] = '/boat/' + id
			self.response.write(json.dumps(curr_boat_dict))
			#self.response.write(curr_boat_dict['name'])

	#delete boat info
	def delete(self, id=None):
		if id:
			del_boat = ndb.Key(urlsafe=id).get()
			#find slip with boat
			boat_name = del_boat.name;
			slips = Slip.query()
			for slip in slips:
				if slip.current_boat == del_boat.id:
					curr_slip = slip
					#empty slip
					curr_slip.current_boat = None
					curr_slip.arrival_date = None
					curr_slip.put()
			#delete boat
			del_boat.key.delete()
			self.response.write('Deleted Boat: ' + boat_name)

	#modify boat info
	def patch(self, id=None):
		if id:
			#get patch data
			updated_boat = json.loads(self.request.body)
			#get current boat data
			curr_boat = ndb.Key(urlsafe=id).get()
			#get updated values
			if updated_boat.get('name'):
				curr_boat.name = updated_boat['name']
			if updated_boat.get('type'):
				curr_boat.type = updated_boat['type']
			if updated_boat.get('length'):
				curr_boat.length = updated_boat['length']
			curr_boat.put()
			curr_boat_dict = curr_boat.to_dict()
			#self.response.write(json.dumps(updated_boat))
			#send updated boat
			self.response.write(json.dumps(curr_boat_dict))
	
	#replace function
	def put(self, id=None):
		if id:
			replace_boat = json.loads(self.request.body)
			curr_boat = ndb.Key(urlsafe=id).get()
			if replace_boat.get('name'):
				curr_boat.name = replace_boat['name']
			else:
				#self.response.write("This stinks")
				self.abort(400)
			if replace_boat.get('type'):
				curr_boat.type = replace_boat['type']
			else:
				curr_boat.type = None
			if replace_boat.get('length'):
				curr_boat.length = replace_boat['length']
			else:
				curr_boat.length = None
			if replace_boat.get('at_sea'):
				curr_boat.at_sea = replace_boat['at_sea']
			curr_boat.put()
			curr_boat_dict = curr_boat.to_dict()
			self.response.write(json.dumps(curr_boat_dict))



#define slip class
class Slip(ndb.Model):
	id = ndb.StringProperty()
	number = ndb.IntegerProperty(required=True)
	current_boat = ndb.StringProperty()
	arrival_date =  ndb.StringProperty()


#manage the slip verbs
class SlipHandler(webapp2.RequestHandler):
	#add new slip
	def post(self):
		#get request body
		slip_data = json.loads(self.request.body)
		#set slip number and to empty
		new_slip = Slip(number=slip_data['number'], current_boat=None, arrival_date=None) #departure_history=None)
		new_slip.put()
		#get and save id
		new_slip.id = new_slip.key.urlsafe()
		new_slip.put()
		slip_dict = new_slip.to_dict()
		slip_dict['self'] = '/slip/' + new_slip.id
		self.response.write(json.dumps(slip_dict))

	#get slip info
	def get(self, id=None):
		if id:
			curr_slip = ndb.Key(urlsafe=id).get()
			curr_slip_dict = curr_slip.to_dict()
			curr_slip_dict['self'] = '/slip/' + id
			self.response.write(json.dumps(curr_slip_dict))

	#modify slip info
	def patch(self, id= None):
		if id:
			updated_slip = json.loads(self.request.body)
			curr_slip = ndb.Key(urlsafe=id).get()
			#modify slip info if changed
			if updated_slip.get('number'):
				curr_slip.number = updated_slip['number']
			if updated_slip.get('current_boat'):
				curr_slip.current_boat = updated_slip['current_boat']
			if updated_slip.get('arrival_date'):
				curr_slip.arrival_date = updated_slip['arrival_date']
			curr_slip.put()
			curr_slip_dict = curr_slip.to_dict()
			self.response.write(json.dumps(curr_slip_dict))

	#delete slip at id
	def delete(self, id=None):
		if id:
			del_slip = ndb.Key(urlsafe=id).get()
			slip_number = del_slip.number
			slip_number = str(slip_number)
			#move boat out to sea
			if del_slip.current_boat:
				boats = Boat.query()
				for boat in boats:
					if boat.id == del_slip.current_boat:
						curr_boat = boat
						curr_boat.at_sea = True
						curr_boat.put()
			#delete slip
			del_slip.key.delete()
			self.response.write('Deleted Slip ' + slip_number)

	#replace slip at id
	def put(self, id=None):
		if id:
			replace_slip = json.loads(self.request.body)
			curr_slip = ndb.Key(urlsafe=id).get()
			#replace slip info if changed and delete all not changed
			if replace_slip.get('number'):
				curr_slip.number = replace_slip['number']
			else:
				self.abort(400)
			if replace_slip.get('current_boat'):
				curr_slip.current_boat = replace_slip['current_boat']
			else:
				curr_slip.current_boat = None
			if replace_slip.get('arrival_date'):
				 curr_slip.arrival_date = replace_slip['arrival_date']
			else:
				curr_slip.arrival_date = None
			curr_slip.put()
			curr_slip_dict = curr_slip.to_dict()
			self.response.write(json.dumps(curr_slip_dict))

	
class BoatListHandler(webapp2.RequestHandler):
	#list all boats
	def get(self):
		boat_list = Boat.query().fetch()
		boat_dict=[]
		#get list into dict
		for item in boat_list:
			item_string = str(item)
			item_string = item_string[40:-1]
			item_string = item_string.replace("=",":")
			item_string = item_string.split(", ")
			boat_dict.append(item_string)	
		self.response.write(json.dumps(boat_dict))


class SlipListHandler(webapp2.RequestHandler):
	#list all Slips
	def get(self):
		slip_list = Slip.query().fetch()
		slip_dict=[]
		#place slip list into dict
		for item in slip_list:
			item_string = str(item)
			item_string = item_string[40:-1]
			item_string = item_string.replace("=",":")
			item_string = item_string.split(", ")
			slip_dict.append(item_string)	
		self.response.write(json.dumps(slip_dict))


#place/delete/get boat in listed slips
class BoatSlipHandler(webapp2.RequestHandler):
	def get(self, id=None):
		curr_boat = None
		if id:
			#get slip info
			curr_slip = ndb.Key(urlsafe=id).get()
			#find boat info of boat occupying slip
			if curr_slip.current_boat:
				boats = Boat.query()
				for boat in boats:
					if boat.id == curr_slip.current_boat:
						curr_boat = boat
						curr_boat_dict = curr_boat.to_dict()
		if curr_boat:
			self.response.write(json.dumps(curr_boat_dict))
		else:
			self.response.write(json.dumps('Slip is currently empty'))
						
	def patch(self, id=None):
		if id:
			#get boat info
			boat_arrival = json.loads(self.request.body)
			curr_slip = ndb.Key(urlsafe=id).get()
			#check if Slip is filled
			if curr_slip.current_boat is not None:
				self.abort(403)
			curr_slip.current_boat = boat_arrival['boat_id']
			curr_slip.arrival_date = boat_arrival['arrival_date']
			boats = Boat.query()
			for boat in boats:
				if boat.id == curr_slip.current_boat:
					curr_boat = boat
					curr_boat.at_sea = False
					curr_boat.put()
			curr_slip_dict = curr_slip.to_dict()
			curr_slip.put()
			self.response.write(json.dumps(curr_slip_dict))

	def delete(self, id=None):
		if id:
			curr_slip = ndb.Key(urlsafe=id).get()
			#query all boats
			boats = Boat.query()
			#get current boat
			for boat in boats:
				if boat.id == curr_slip.current_boat:
					curr_boat = boat
					curr_boat_dict = curr_boat.to_dict()
					#set boat to at_sea
					curr_boat.at_sea = True
					curr_boat.put()
			#set ship status to empty
			curr_slip.current_boat = None
			curr_slip.arrival_date = None
			curr_slip_dict = curr_slip.to_dict()
			curr_slip.put()
			self.response.write(json.dumps(curr_slip_dict))			

#test class
class MainPage(webapp2.RequestHandler):
    def get(self):
        self.response.write('Hello, this is just a test page3')

#create patch method
allowed_methods = webapp2.WSGIApplication.allowed_methods
new_allowed_methods = allowed_methods.union(('PATCH',))
webapp2.WSGIApplication.allowed_methods = new_allowed_methods

#url routing
app = webapp2.WSGIApplication([
    ('/', MainPage),
    ('/boat', BoatHandler),
    ('/boats', BoatListHandler),
    ('/slip/(.*)/boat', BoatSlipHandler),
    ('/slip', SlipHandler),
    ('/slips', SlipListHandler),
    ('/slip/(.*)', SlipHandler),
    ('/boat/(.*)', BoatHandler)
], debug=True)

#exection handlers
#def handle_401(request, response, exception):
 #   logging.exception(exception)
  #  response.write("Required Key Not Included")
#    response.set_status(401)

#def handle_403(request, response, exception):
 #   logging.exception(exception)
  #  response.write("Slip is Occupied")
   # response.set_status(403)
